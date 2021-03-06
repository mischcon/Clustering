package de.oth.clustering.scala.vm

import akka.actor.{Actor, ActorLogging}
import de.oth.clustering.scala.vm.messages.VmTask

/**
  * Created by oliver.ziegert on 02.05.17.
  */
class VMActorHelper extends Actor with ActorLogging {

  override def receive: Receive = {
    case VmTask(runnable) => log.debug(s"got VmTask($runnable)"); runnable.run(); context.stop(self)
  }

  override def preStart = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop = {
    log.debug(s"goodbye from ${self.path.name}")
  }

}
