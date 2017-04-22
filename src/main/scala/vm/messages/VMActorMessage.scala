package vm.messages

import akka.actor.ActorRef
import vm.vagrant.configuration.VagrantEnvironmentConfig

/**
  * Created by oliver.ziegert on 22.04.17.
  */
trait VMActorMessage

case class SetVmEnvironment(vmEnvironment: VagrantEnvironmentConfig) extends VMActorMessage

case class SetNodeMonitorActor(nodeMonitorActor: ActorRef) extends VMActorMessage
