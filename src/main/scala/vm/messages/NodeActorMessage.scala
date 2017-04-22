package vm.messages

import vm.vagrant.configuration.VagrantEnvironmentConfig

/**
  * Created by oliver.ziegert on 22.04.17.
  */
trait NodeActorMessage

case class SetMonitorMaster(master: String) extends NodeActorMessage

case class SetVmEnvironment(vmEnvironment: VagrantEnvironmentConfig) extends NodeActorMessage

case object StopMonitoring extends NodeActorMessage

case object StartMonitoring extends NodeActorMessage
