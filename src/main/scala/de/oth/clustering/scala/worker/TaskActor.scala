package de.oth.clustering.scala.worker

import de.oth.clustering.scala.exceptions._
import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{ActorRef, Deploy, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.pattern._
import akka.remote.RemoteScope
import akka.util.Timeout
import de.oth.clustering.scala.utils.db.{EndState, TaskStatus, UpdateTask, UpdateTaskStatus}
import de.oth.clustering.scala.utils.messages.{ExecutorAddress, GetExecutorAddress}
import de.oth.clustering.scala.worker.messages._
import de.oth.clustering.scala.worker.traits.WorkerTrait

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Random, Success}

/**
  * There is one actor of this kind for every task. If it is free (its task is currently to being executed)
  * it will offer itsself if it receives a GetTask request from a {@link de.oth.clustering.scala.vm#VMProxyActor VMProxyActor}. If the sender is happy with
  * this task than the {@link de.oth.clustering.scala.worker.TaskActor TaskActor} will request the address of a physical node from the {@link de.oth.clustering.scala.utils.ExecutorDirectoryServiceActor ExecutorDirectoryServiceActor}
  * in order to create a new remote {@link de.oth.clustering.scala.worker.TaskExecutorActor TaskExecutorActor}. After that the task is passed to the {@link de.oth.clustering.scala.worker.TaskExecutorActor TaskExecutorActor} which
  * itsself executes the task against the target {@link de.oth.clustering.scala.vm#VMProxyActor VMProxyActor}.
  *
  * @param task The actual task
  * @param tablename The corresponding tablename / run id
  */
class TaskActor(task : Task, tablename : String) extends WorkerTrait{

  var isTaken : Boolean = false
  var taskDone : Boolean = false

  var targetVm : ActorRef = null
  var executorActor : ActorRef = null

  implicit val timeout = Timeout(1 seconds)
  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  override def preStart(): Unit = {
    super.preStart()
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug(s"Goodbye from ${self.path.name}")
  }

  /**
    * Whether or not a task was successful an exception ({@link de.oth.clustering.scala.Exceptions#TestSuccessException TestSuccessException}
    * or {@link de.oth.clustering.scala.Exceptions#TestFailException TestFailException})
    * will be thrown by the {@link de.oth.clustering.scala.worker.TaskExecutorActor TaskExecutorActor}.
    * In case the task was successful the 'taskDone' flag is set to true.
    * In case the task was unsuccessful the 'taskDone' flag also set to true.
    * Only in case of an unexpected exception the 'taskDone' flag is set to false, resulting
    * in the task being executed again.
    *
    * What happens if the targetVm dies?
    * 1) The cluster will realize that --> Terminated message
    * 2) The executor will realize that (since some unexpected exceptions will occur) --> TestFailException will be thrown
    *
    * But what happens if those messages reach the TaskActor?:
    *   1) Executor(Success) > VM(Termianted) --> all good, since Executor(Success) causes the 'taskDone' flag to be set to True
    *   2) Executor(Failure) > VM(Termianted) --> nothing good, since we need to provide a directive BEFORE we can
    *   handle the Terminated message without knowing if the VM died or not (without being able to check whether or
    *   not there is a Termianted message); In this case we 'ask' the VMProxyActor if it is still alive / still can
    *   connect to the VM -- if so, then everything is good, if not - reset the task and execute it once more
    *   on a different VM
    *   3) VM(Terminated) > Executor(Success) --> all good, we do not trust the TestSuccessException and reset the task
    *   4) VM(Terminated) > Executor(Failure) --> all good, simply reset the task
    *
    * @return
    */
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){
    case _ if !isTaken => {
      /* There is only one condition under which this could happen:
      *   The de.oth.clustering.scala.vm died (isTaken = false) and the executor has been stopped (context.stop()), but the
      *   Exception is already in the mailbox - in this case it is irrelevant what directive you choose,
      *   since the executor already died. To be on the safe side we chose 'Stop' */
      log.debug("racy racy race-condition - received an exception from a (hopefully) dead executor... how scary oO")
      Stop
    }
    case t : TestSuccessException => {
      log.debug("received TestSuccessException!")
      taskDone = true
      Escalate
    }
    case t : TestFailException => {
      log.debug("received TestFailException!")

      //ask the de.oth.clustering.scala.vm if it is still alive
      val stillAliveFuture = targetVm.ask(StillAlive)(timeout = 11 seconds, self)
      val alive = Await.result(stillAliveFuture, 10 seconds)
      alive match {
        case true => log.debug("de.oth.clustering.scala.vm is still alive - escalating..."); taskDone = true; Escalate
        case a => log.debug(s"de.oth.clustering.scala.vm is NOT alive anymore (received: ${a}) - the TestFailException seems to be caused by this - resetting the task..."); taskDone = false; Stop
      }
    }
    case a => {
      log.debug(s"received an unexpected exception - resetting: ${a}")
      taskDone = false
      Stop
    }
  }

  override def receive: Receive = {
    case p : GetTask if ! isTaken => handleGetTask()
    case t : Terminated => handleTermianted(t)
    case a : PersistAndSuicide => {
      log.debug("received PersistAndSuicide")
      context.system.actorSelection("/user/db") ! UpdateTask(s"${task.classname}.${task.method}", TaskStatus.NOT_STARTED, EndState.ABANDONED, s"DEPENDENCY FAILED: ${a.reason}", tablename)
      context.stop(self)
    }
  }

  /**
    * If the {@link de.oth.clustering.scala.worker.TaskActor TaskActor} receives a 'Terminated' message from the actor system than this means
    * that either the {@link de.oth.clustering.scala.vm#VMActor VMActor} or the {@link de.oth.clustering.scala.worker.TaskExecutorActor TaskExecutorActor} died / has been disconnected from the cluster.
    * In case the 'taskDone' flag is set to true this is the desired behaviour since the {@link de.oth.clustering.scala.worker.TaskExecutorActor TaskExecutorActor}
    * is stopped after the execution of a task.
    * In case the 'taskDone' flag is set to false this means that either the {@link de.oth.clustering.scala.vm#VMActor VMActor} or the {@link de.oth.clustering.scala.worker.TaskExecutorActor TaskExecutorActor} died, which
    * results in removing the connection to the {@link de.oth.clustering.scala.vm#VMActor VMActor} / {@link de.oth.clustering.scala.worker.TaskExecutorActor TaskExecutorActor} and resetting the task so that it can
    * be executed again.
    * @param t The Terminated message - used for identifying the terminated actor.
    */
  def handleTermianted(t : Terminated) = {
    if(taskDone) {
      log.debug("Terminated + task done --> shutting down self")
      context.stop(self)
    } else {
      log.debug("Terminated + NOT task done --> isTaken = false + targetVm = null")
      isTaken = false

      // check what crashed - the targetVM or the executor
      if(t.actor == targetVm){
        // de.oth.clustering.scala.vm died - kill the executor, but unwatch it first
        context.unwatch(executorActor)
        //executorActor ! Kill
        context.stop(executorActor)
        executorActor = null
      }

      context.unwatch(targetVm)
      targetVm = null

      // updating database
      context.system.actorSelection("/user/db") ! UpdateTaskStatus(s"${task.classname}.${task.method}", TaskStatus.NOT_STARTED, tablename)
    }
  }

  /**
    * If a new task is requested and the task has not been taken / reserved yet than the {@link de.oth.clustering.scala.worker.TaskActor TaskActor}
    * will offer itself ({@link de.oth.clustering.scala.worker.messages#SendTask SendTask} to requester). To avoid being executed twice the 'isTaken' flag is set to true -
    * in case the requester has chosen a different task the 'isTaken' flag is set to false, resulting in the task
    * to be available again.
    *
    * In case the requester chose this task the {@link de.oth.clustering.scala.worker.TaskActor TaskActor} will watch / supervise the requester (in order to
    * reset the task in case of a failure of the requester) and will request an executor from the {@link de.oth.clustering.scala.utils.ExecutorDirectoryServiceActor ExecutorDirectoryServiceActor}.
    *
    * If no executor is available the requester is informed about that and the task is being resetted.
    * If an executor is available the {@link de.oth.clustering.scala.worker.TaskActor TaskActor} will watch / supervise the executor (in order to reset the task and
    * the requester in case of a failure of the executor) and will send the reference of the executor to the requester.
    *
    * If the connection between requester (targetVM), executor and {@link de.oth.clustering.scala.worker.TaskActor TaskActor} has been established the task is sent
    * to the executor for its execution.
    */
  def handleGetTask() = {
    //log.debug(s"received GetTask and I am not taken! - sending SendTask to ${sender().path.toString}")
    isTaken = true
    val executor = sender() ? SendTask(task)
    executor.onComplete{
      case Success(target : AcquireExecutor) => {
        log.debug("received vmInfo - now creating Executor")

        // watch SENDER (the targetVM Actor)
        targetVm = target.vmActorRef
        context.watch(targetVm)

        // check if the task should be run on the same physical node
        if(task.run_locally) {
          var executorAddress = targetVm.path.address
          executorActor = context.actorOf(Props[TaskExecutorActor].withDeploy(
            Deploy(scope = RemoteScope(executorAddress))
          ), s"EXECUTOR-${task.method}-${new Random().nextLong()}")
          monitor_send_and_execute()
        } else {
          // get executor
          (context.system.actorSelection("/user/ExecutorDirectory") ? GetExecutorAddress) onComplete {
            case Success(addr: ExecutorAddress) => {
              executorActor = context.actorOf(Props[TaskExecutorActor].withDeploy(
                Deploy(scope = RemoteScope(addr.address))
              ), s"EXECUTOR-${task.method}-${new Random().nextLong()}")
              monitor_send_and_execute()
            }
            case Failure(_) => {
              //log.error("could not get an executor - sending CannotGetExecutor to targetVm and goind back to isTaken = false")
              targetVm ! CannotGetExecutor
              isTaken = false
            }
          }
        }

        def monitor_send_and_execute() = {
          // monitor executor
          context.watch(executorActor)

          // send actorRef to targetVm
          log.debug("sending actorRef of EXECUTOR to targetVmActor")
          targetVm ! Executor(executorActor)

          log.debug("sending ExecuteTask to EXECUTOR")
          executorActor ! ExecuteTask(task, target.vmActorRef)

          // updating database
          context.system.actorSelection("/user/db") ! UpdateTaskStatus(s"${task.classname}.${task.method}", TaskStatus.RUNNING, tablename)
        }
      }
      case Failure(exception) => {
        //log.error("did not receive vmInfos - going back to isTaken = false")
        isTaken = false
      }
    }


  }
}
