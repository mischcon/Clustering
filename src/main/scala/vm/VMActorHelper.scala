package vm

import akka.actor.{Actor, ActorLogging}
import vm.messages.VmTask

/**
  * Created by oliver.ziegert on 02.05.17.
  */
class VMActorHelper extends Actor with ActorLogging {

  override def receive: Receive = {
    case VmTask(runnable) => log.debug(s"got mTask($runnable)"); new Thread(runnable).start()
  }

}
