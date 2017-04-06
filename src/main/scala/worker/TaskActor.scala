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

class TaskActor(task : Task) extends WorkerTrait{

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
      // TODO: tableName context.system.actorSelection("/user/db") ! UpdateTask(s"${task.classname}.${task.method}", TaskStatus.NOT_STARTED, EndState.FAILURE, s"DEPENDENCY FAILED: ${a.reason}")
      context.stop(self)
    }
  }

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
      // TODO: tableName context.system.actorSelection("/user/db") ! UpdateTaskStatus(s"${task.classname}.${task.method}", TaskStatus.NOT_STARTED)
    }
  }

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
            executorActor ! ExecuteTask(task, target.vmInfo)

            // updating database
            // TODO: tableName context.system.actorSelection("/user/db") ! UpdateTaskStatus(s"${task.classname}.${task.method}", TaskStatus.RUNNING)
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
