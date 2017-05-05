package worker

import Exceptions._
import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{ActorRef, Deploy, Kill, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.pattern._
import akka.remote.RemoteScope
import akka.util.Timeout
import utils.db.{EndState, TaskStatus, UpdateTask, UpdateTaskStatus}
import utils.messages.{ExecutorAddress, GetExecutorAddress}
import worker.messages._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

/**
  * There is one actor of this kind for every task. If it is free (its task is currently to being executed)
  * it will offer itsself if it receives a GetTask request from a {@link vm#VMProxyActor VMProxyActor}. If the sender is happy with
  * this task than the {@link worker.TaskActor TaskActor} will request the address of a physical node from the {@link utils.ExecutorDirectoryServiceActor ExecutorDirectoryServiceActor}
  * in order to create a new remote {@link worker.TaskExecutorActor TaskExecutorActor}. After that the task is passed to the {@link worker.TaskExecutorActor TaskExecutorActor} which
  * itsself executes the task against the target {@link vm#VMProxyActor VMProxyActor}.
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
    * Whether or not a task was successful an exception ({@link Exceptions#TestSuccessException TestSuccessException}
    * or {@link Exceptions#TestFailException TestFailException})
    * will be thrown by the {@link worker.TaskExecutorActor TaskExecutorActor}.
    * In case the task was successful the 'taskDone' flag is set to true.
    * In case the task was unsuccessful the 'taskDone' flag also set to true.
    * Only in case of an unexpected exception the 'taskDone' flag is set to false, resulting
    * in the task being executed again.
    * @return
    */
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){
    case t : TestSuccessException => {
      log.debug("received TestSuccessException!")
      taskDone = true
      Escalate
    }
    case t : TestFailException => {
      // TODO: check whether or not the VM has failed - in this case this TestFailException might not be caused by a "failed" test, but because of the failed VM
      log.debug("received TestFailException!")
      taskDone = true
      Escalate
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
      context.system.actorSelection("/user/db") ! UpdateTask(s"${task.classname}.${task.method}", TaskStatus.NOT_STARTED, EndState.FAILURE, s"DEPENDENCY FAILED: ${a.reason}", tablename)
      context.stop(self)
    }
  }

  /**
    * If the {@link worker.TaskActor TaskActor} receives a 'Terminated' message from the actor system than this means
    * that either the {@link vm#VMActor VMActor} or the {@link worker.TaskExecutorActor TaskExecutorActor} died / has been disconnected from the cluster.
    * In case the 'taskDone' flag is set to true this is the desired behaviour since the {@link worker.TaskExecutorActor TaskExecutorActor}
    * is stopped after the execution of a task.
    * In case the 'taskDone' flag is set to false this means that either the {@link vm#VMActor VMActor} or the {@link worker.TaskExecutorActor TaskExecutorActor} died, which
    * results in removing the connection to the {@link vm#VMActor VMActor} / {@link worker.TaskExecutorActor TaskExecutorActor} and resetting the task so that it can
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

      /* TODO: if the VM dies the test will fail probably because of an IO Exception or something like that.
      * In this case the Test has not failed because of some assertation error or things like that, which means
      * that the task is not done / the test needs to be rerun
      *
      * NEEDS TO BE IMPLEMENTED!
      * */

      // check what crashed - the targetVM or the executor
      if(t.actor == targetVm){
        // vm died - kill the executor, but unwatch it first
        context.unwatch(executorActor)
        executorActor ! Kill
        executorActor = null
      }

      context.unwatch(targetVm)
      targetVm = null

      // updating database
      context.system.actorSelection("/user/db") ! UpdateTaskStatus(s"${task.classname}.${task.method}", TaskStatus.NOT_STARTED, tablename)
    }
  }

  /**
    * If a new task is requested and the task has not been taken / reserved yet than the {@link worker.TaskActor TaskActor}
    * will offer itself ({@link worker.messages#SendTask SendTask} to requester). To avoid being executed twice the 'isTaken' flag is set to true -
    * in case the requester has chosen a different task the 'isTaken' flag is set to false, resulting in the task
    * to be available again.
    *
    * In case the requester chose this task the {@link worker.TaskActor TaskActor} will watch / supervise the requester (in order to
    * reset the task in case of a failure of the requester) and will request an executor from the {@link utils.ExecutorDirectoryServiceActor ExecutorDirectoryServiceActor}.
    *
    * If no executor is available the requester is informed about that and the task is being resetted.
    * If an executor is available the {@link worker.TaskActor TaskActor} will watch / supervise the executor (in order to reset the task and
    * the requester in case of a failure of the executor) and will send the reference of the executor to the requester.
    *
    * If the connection between requester (targetVM), executor and {@link worker.TaskActor TaskActor} has been established the task is sent
    * to the executor for its execution.
    */
  def handleGetTask() = {
    log.debug(s"received GetTask and I am not taken! - sending SendTask to ${sender().path.toString}")
    isTaken = true
    val executor = sender() ? SendTask(task)
    executor.onComplete{
      case Success(target : AcquireExecutor) => {
        log.debug("received vmInfo - now creating Executor")

        // watch SENDER (the targetVM Actor)
        targetVm = target.vmActorRef
        context.watch(targetVm)

        // get executor
        (context.system.actorSelection("/user/ExecutorDirectory") ? GetExecutorAddress) onComplete{
          case Success(addr : ExecutorAddress) => {
            executorActor = context.actorOf(Props[TaskExecutorActor].withDeploy(
              Deploy(scope = RemoteScope(addr.address))
            ), s"EXECUTOR-${task.method}-${new Random().nextLong()}")

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
          case Failure(_) => {
            log.error("could not get an executor - sending CannotGetExecutor to targetVm and goind back to isTaken = false")
            targetVm ! CannotGetExecutor
            isTaken = false
          }
        }
      }
      case Failure(exception) => {
        log.error("did not receive vmInfos - going back to isTaken = false")
        isTaken = false
      }
    }
  }
}
