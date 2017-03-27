package worker

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.remote.ContainerFormats.ActorRef
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
      context.system.actorSelection("/user/distributor") ! GetTask()
    }
    case t : SendTask if haveSpaceForTasks => {
      log.debug("received SendTask and I still have space for tasks!")
      haveSpaceForTasks = false

      sender() ! AquireExecutor(vmInfo, self)
    }
    case t : SendTask if ! haveSpaceForTasks => {
      log.debug("received SendTask but I dont have any more space :(")
    }
    case t : Executor => {
      log.debug("received an ActorRef, which means that this is an executor - monitoring it now")
      context.watch(t.ref)
    }
    case t : Terminated => {
      log.debug("received TERMINATED, which means that the task is done - now I have space for a new task!")
      haveSpaceForTasks = true
    }
    case a => log.error(s"received something unexpected: ${a}")
  }
}
