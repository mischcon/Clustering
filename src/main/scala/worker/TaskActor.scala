package worker

import Exceptions._
import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{ActorRef, Deploy, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.pattern._
import akka.remote.RemoteScope
import akka.util.Timeout
import utils.messages.{ExecutorAddress, GetExecutorAddress}
import worker.messages._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

/**
  * Created by mischcon on 21.03.17.
  */
class TaskActor(task : Task) extends WorkerTrait{

  var isTaken : Boolean = false
  var taskDone : Boolean = false

  var targetVm : ActorRef = null
  var executorActor : ActorRef = null

  implicit val timeout = Timeout(1 seconds)
  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  override def preStart(): Unit = {
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"Goodbye from ${self.path.name}")
  }

  /*
  * POSITIVE PATH: if the task has been done
  *
  * Success: Stop the ExecutorActor
  * Failure: Inform the parent actor --> he should stop all other child actors
  *
  * */
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){
    case t : TestSuccessException => {
      log.debug("received TestSuccessException!")
      taskDone = true
      Escalate
    }
    case t : TestFailException => {
      log.debug("received TestFailException!")
      taskDone = true
      Escalate
    }
    case a : Throwable => {
      log.debug(s"received an unexpected exception: ${a.getMessage}")
      Escalate
    }
  }

  /*
  * Terminated: This event can occur because of two different causes
  * 1) The executing actor has been shutted down because of the STOP directive
  *    from the supervisorStrategy above (implies that taskDone = true)
  *    In this case simply shut down the TaskActor because it has done its work
  * 2) The executing actor is UNREACHABLE because the node has been disconnected
  *    In this case taskDone = false, which means that the task still needs to be
  *    executed (re-run) - so simply set isTaken back to false and wait until
  *    another Executor asks for a new task
  * */
  override def receive: Receive = {
    case p : GetTask if ! isTaken => handleGetTask()
    case Terminated => handleTermianted()
  }

  def handleTermianted() = {
    if(taskDone) {
      log.debug("Terminated + task done --> shutting down self")
      context.stop(self)
    } else {
      log.debug("Terminated + NOT task done --> isTaken = false + targetVm = null")
      isTaken = false
      context.unwatch(targetVm)
      targetVm = null
      context.unwatch(executorActor)
      executorActor = null
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
          }
          case Failure(e) => {
            /* IMPROVEMENT NEEDED
            *
            * If we are unable to get an executor just send a TERMINATED message to the target VM -
            * currently this will destroy
            * */
            log.error("could not get an executor - sending TERMINATED to targetVm and goind back to isTaken = false")
            targetVm ! Terminated
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
