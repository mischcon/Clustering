package vm

import akka.actor.{Actor, ActorLogging}
import vm.messages.VmTask

/**
  * Created by oliver.ziegert on 02.05.17.
  */
class VMActorHelper extends Actor with ActorLogging {

  override def receive: Receive = {
    case VmTask(runnable) => log.debug(s"got VmTask($runnable)"); new Thread(runnable).start()
  }

  override def preStart = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop = {
    log.debug(s"goodbye from ${self.path.name}")
  }

}
