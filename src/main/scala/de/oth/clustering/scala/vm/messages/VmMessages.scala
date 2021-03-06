package de.oth.clustering.scala.vm.messages

import java.io.File

import akka.actor.{ActorRef, Address}
import de.oth.clustering.java.vm.vagrant.configuration.{VagrantEnvironmentConfig, VagrantVmConfig}

/**
  * Created by oliver.ziegert on 22.04.17.
  */
trait VmMessages extends Serializable

case object Init extends VmMessages

case class SetGlobalStatusActor(globalStatusActor: ActorRef) extends VmMessages

case object GetGlobalStatusActor extends VmMessages

case class SetInstanceActor(instanceActor: ActorRef) extends VmMessages

case object GetInstanceActor extends VmMessages

case class DeregisterNodeActor(actorRef: ActorRef) extends VmMessages

case class SetNodeMasterActorPath(path: String) extends VmMessages

case class SetNodeMasterActor(actor: ActorRef) extends VmMessages

case class IncludeNode(address: Address) extends VmMessages

case class SetVmProxyActor(vmProxyActor: ActorRef) extends VmMessages

case class GetVmProxyActor(sender: ActorRef) extends VmMessages

case class SetVmActor(vmActor: ActorRef) extends VmMessages

case class GetVmActor(sender: ActorRef) extends VmMessages

case object VmProvisioned extends VmMessages

case class SetVagrantEnvironmentConfig(vagrantEnvironmentConfig: VagrantEnvironmentConfig) extends VmMessages

case object GetVagrantEnvironmentConfig extends VmMessages

case class NotReadyJet(any: Any) extends VmMessages

case class SetDistributorActor(distributor: ActorRef) extends VmMessages

case object GetDistributorActor extends VmMessages

case object AddVmActor extends VmMessages

case class RemoveVmActor(self: ActorRef) extends VmMessages

case class VmTask(runnable: Runnable) extends VmMessages

case class VmTaskResult(any: Any) extends VmMessages

case object TasksDone extends VmMessages





// VMControl
case class VmUp(box: VagrantVmConfig = null) extends VmMessages
case class VmDestroy(box: VagrantVmConfig = null) extends VmMessages
case class VmHalt(box: VagrantVmConfig = null) extends VmMessages
case class VmResume(box: VagrantVmConfig = null) extends VmMessages
case class VmReload(box: VagrantVmConfig = null) extends VmMessages
case class VmSuspend(box: VagrantVmConfig = null) extends VmMessages
case class VmStatus(box: VagrantVmConfig = null) extends VmMessages
case class VmInit(boxName: String = null) extends VmMessages
case class VmProvision(box: VagrantVmConfig = null) extends VmMessages
case class VmSnapshotPush(box: VagrantVmConfig = null) extends VmMessages
case class VmSnapshotPop(box: VagrantVmConfig = null) extends VmMessages
case class VmSshExecute(box: VagrantVmConfig, command: String) extends VmMessages
case object VmGetAllAvailableBoxes extends VmMessages
case class VmAddBoxe(boxName: String) extends VmMessages
case class VmRemoveBoxes(boxName: String) extends VmMessages
case class VmUpdateBoxes(boxName: String = null) extends VmMessages
case class VmResponse(response: Any) extends VmMessages








case object StopMonitoring extends VmMessages

case object StartMonitoring extends VmMessages


// NodeMonitor
case object GetSystemAttributes extends VmMessages

case class SetPath(path : File) extends VmMessages



// VMActor

case class SetNodeMonitorActor(nodeMonitorActor: ActorRef) extends VmMessages

case object GetNodeMonitorActor extends VmMessages