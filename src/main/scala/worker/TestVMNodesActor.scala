package worker

import akka.actor.Status.Failure
import akka.actor.Terminated
import worker.messages._

/**
  * Created by mischcon on 26.03.2017.
  */
class TestVMNodesActor(vmInfo : Object) extends WorkerTrait{

  var haveSpaceForTasks = true

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case "get" => {
      log.debug("sent GetTask to distributor")
      context.system.actorSelection("/user/instances") ! GetTask()
    }
    case t : SendTask if haveSpaceForTasks => {
      log.debug("received SendTask and I still have space for tasks!")
      haveSpaceForTasks = false

      log.debug(s"sending AquireExecutor to ${t.source}")
      t.source ! AcquireExecutor(vmInfo, self)
    }
    case t : SendTask if ! haveSpaceForTasks => {
      log.debug("received SendTask but I dont have any more space :(")
      t.source ! Failure(new Exception)
    }
    case t : Executor => {
      log.debug("received an ActorRef, which means that this is an executor - monitoring it now")
      context.watch(t.ref)
    }
    case t : Terminated => {
      log.debug(s"received TERMINATED from ${t.actor.path.toString}, which means that the task is done - now I have space for a new task!")
      handleFailure()
    }
    case CannotGetExecutor => handleFailure()
    case NoMoreTasks => {
      log.debug("it seems as if there are no more tasks - shutting down self")
      context.stop(self)
    }
    case a => log.error(s"received something unexpected: ${a}")
  }

  def handleFailure(): Unit ={
    log.debug("releasing task - now I have space for a new task!")
    haveSpaceForTasks = true

    Thread.sleep(1000)
    self ! "get"
  }
}
