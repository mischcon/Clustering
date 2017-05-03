package vm


import java.io.File

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}
import akka.pattern._
import akka.util.Timeout
import vm.messages._
import vm.vagrant.Vagrant
import vm.vagrant.configuration.{VagrantEnvironmentConfig, VagrantVmConfig}
import vm.vagrant.model.VagrantEnvironment
import vm.vagrant.util.VagrantException
import worker.messages.{DeployInfo, GetDeployInfo, NoDeployInfo}

import scala.collection.JavaConverters.{asJavaIterableConverter, iterableAsScalaIterableConverter}
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
  * Created by mischcon on 3/20/17.
  */
class VMActor extends Actor with ActorLogging {

  private var uuid: String = _
  private var nodeActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _
  private var vagrantEnvironment: VagrantEnvironment = _
  private var vmProxyActor: ActorRef = _
  private var vmProvisioned: Boolean = _
  private var cancellable: Cancellable = _

  self ! Init

  override def receive: Receive = {
    case Init => init
    case GetVagrantEnvironmentConfig if vmProvisioned => sender() ! SetVagrantEnvironmentConfig(vagrantEnvironmentConfig)
    case VmUp(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.up(box))
    case VmDestroy(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.destroy(box))
    case VmHalt(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.halt(box))
    case VmResume(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.resume(box))
    case VmReload(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.reload(box))
    case VmSuspend(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.suspend(box))
    case VmStatus(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.status(box))
    case VmInit(boxName) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.init(boxName))
    case VmProvision(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.provision(box))
    case VmSnapshotPush(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.snapshotPush(box))
    case VmSnapshotPop(box) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.snapshotPop(box))
    case VmSshExecute(box, command) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.sshExecute(box, command))
    case VmGetAllAvailableBoxes if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.getAllAvailableBoxes)
    case VmAddBoxe(boxName) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.addBoxe(boxName))
    case VmRemoveBoxes(boxName) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.removeBoxes(boxName))
    case VmUpdateBoxes(boxName) if vmProvisioned => sender() ! VmResponse(vagrantEnvironment.updateBoxes(boxName))
    case _ if !vmProvisioned => sender() ! NotReadyJet
    case DeployInfo(vagrantEnvironmentConfig) => {
      log.debug("received DeployInfo")
      this.vagrantEnvironmentConfig = vagrantEnvironmentConfig
      if (cancellable != null) cancellable.cancel()
      provisionVm
    }
    case NoDeployInfo => registerScheduler
  }

  private def init = {
    uuid = self.path.name.split("_"){1}
    nodeActor = context.parent
    vmProvisioned = false
    implicit val timeout = Timeout(5 seconds)
    val instanceActorFuture = nodeActor ? GetInstanceActor
    Await.result(instanceActorFuture, timeout.duration) match {
      case SetInstanceActor(instanceActor) => this.instanceActor = instanceActor
      case _ => ???
    }
    val vmProxyActorFuture = nodeActor ? GetVmProxyActor
    Await.result(vmProxyActorFuture, timeout.duration) match {
      case SetVmProxyActor(vmProxyActor) => this.vmProxyActor = vmProxyActor
      case _ => ???
    }
    instanceActor ! GetDeployInfo
  }

  private def provisionVm = {
    if (!vagrantEnvironmentConfig.path().isDirectory)
      throw new VagrantException("path is not a directory!")
    if (vagrantEnvironmentConfig.path().canWrite)
      throw new VagrantException("can not write in path")
    val path = new File(vagrantEnvironmentConfig.path(), uuid)
    val version = vagrantEnvironmentConfig.version()
    path.mkdirs()
    vagrantEnvironment = new Vagrant().createEnvironment(vagrantEnvironmentConfig)
    vagrantEnvironment.up()
    var vmConfigs = vagrantEnvironmentConfig.vmConfigs().asScala.map(vagrantEnvironment.getBoxePortMapping(_))
    vagrantEnvironmentConfig = new VagrantEnvironmentConfig(vmConfigs.asJava, vagrantEnvironmentConfig.path())
    vagrantEnvironmentConfig.setVersion(version)
    vmProvisioned = true
    nodeActor ! VmProvisioned
    vmProxyActor ! SetVagrantEnvironmentConfig(vagrantEnvironmentConfig)
  }

  private def registerScheduler = {
    if (cancellable != null) cancellable.cancel()
    import context.dispatcher
    cancellable = context.system.scheduler.schedule(10 seconds, 60 seconds, instanceActor, GetDeployInfo)
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    if (vagrantEnvironment != null)
      vagrantEnvironment.destroy()
    log.debug(s"goodbye from ${self.path.name}")
  }
}
