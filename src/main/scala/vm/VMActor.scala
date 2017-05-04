package vm


import java.io.File
import java.nio.file.Files
import java.util.Random

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.util.Timeout
import com.rits.cloning.Cloner
import vm.messages._
import vm.vagrant.Vagrant
import vm.vagrant.configuration.VagrantEnvironmentConfig
import vm.vagrant.model.VagrantEnvironment
import vm.vagrant.util.VagrantException
import worker.messages.{DeployInfo, GetDeployInfo, NoDeployInfo}

import scala.collection.JavaConverters.{asJavaIterableConverter, iterableAsScalaIterableConverter}
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
  private var path: File = _
  private var version: String = _
  implicit val timeout = Timeout(5 seconds)

  self ! Init

  override def receive: Receive = {
    case Init => init
    case GetVagrantEnvironmentConfig if vmProvisioned => sender() ! SetVagrantEnvironmentConfig(vagrantEnvironmentConfig)
    case SetInstanceActor(instanceActor) => this.instanceActor = instanceActor; initGetDeployInfo
    case SetVmProxyActor(vmProxyActor) => this.vmProxyActor = vmProxyActor; initGetDeployInfo
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
    case DeployInfo(vagrantEnvironmentConfig) => {
      log.debug("received DeployInfo")
      this.vagrantEnvironmentConfig = new Cloner().deepClone(vagrantEnvironmentConfig)
      if (cancellable != null) cancellable.cancel()
      startProvisionVm
    }
    case NoDeployInfo => registerScheduler
    case VmTaskResult(any) => {
      any match {
        case x: (VagrantEnvironment, String, VagrantEnvironmentConfig, Iterator[(String, vm.vagrant.model.VmStatus.Value)]) => endProvisionVm(x)
        case x: VmDestroy => nodeActor ! RemoveVmActor(self)
      }
    }
    case TasksDone => tasksDone
    case _ if !vmProvisioned => sender() ! NotReadyJet
  }

  private def init = {
    log.debug("init called")
    uuid = self.path.name.split("_"){1}
    nodeActor = context.parent
    vmProvisioned = false
    nodeActor ! GetInstanceActor
    nodeActor ! GetVmProxyActor(self)
  }

  private def initGetDeployInfo = {
    if (instanceActor != null && vmProxyActor != null)
      instanceActor ! GetDeployInfo
  }

  private def startProvisionVm = {
    log.debug("provisionVm called")
    log.debug(s"path is ${vagrantEnvironmentConfig.path()}")
    if (!vagrantEnvironmentConfig.path().isDirectory)
      throw new VagrantException("path is not a directory!")
    path = new File(vagrantEnvironmentConfig.path(), uuid)
    version = vagrantEnvironmentConfig.version()
    path.mkdirs()
    vagrantEnvironmentConfig = new VagrantEnvironmentConfig(vagrantEnvironmentConfig.vmConfigs(), path)
    vagrantEnvironmentConfig.setVersion(version)

    var vmCounter = 0
    vagrantEnvironmentConfig.vmConfigs().asScala.foreach(d => {
      if (d.provider().vmName() != null && d.provider().vmName().length > 0 )
        d.provider().setVmName(s"${d.provider().vmName()}_$uuid")
      else {
        vmCounter += 1
        d.provider().setVmName(s"${uuid}_$vmCounter")
      }
    })
    val runnable: Runnable = () => {
      val vmActor = self
      vagrantEnvironment = new Vagrant().createEnvironment(vagrantEnvironmentConfig)
      var output = ""
      try {
        vagrantEnvironment.destroy()
        output = vagrantEnvironment.up()
      } catch {
        case e: Exception => {
          vagrantEnvironment.destroy()
          sbt.io.IO.delete(path)
          instanceActor.tell(GetDeployInfo, vmActor)
        }
      }
      val vmConfigs = vagrantEnvironmentConfig.vmConfigs().asScala.map(vagrantEnvironment.getBoxePortMapping(_))
      vagrantEnvironmentConfig = new VagrantEnvironmentConfig(vmConfigs.asJava, vagrantEnvironmentConfig.path())
      vagrantEnvironmentConfig.setVersion(version)
      vmActor ! VmTaskResult((vagrantEnvironment, output, vagrantEnvironmentConfig, vagrantEnvironment.status()))
    }
    val vmActorHelper: ActorRef = context.actorOf(Props[VMActorHelper], s"vmActorHelper_${new Random().nextLong()}")
    vmActorHelper ! VmTask(runnable)
  }

  private def endProvisionVm(x: (VagrantEnvironment, String, VagrantEnvironmentConfig, Iterator[(String, vm.vagrant.model.VmStatus.Value)])) = {
    if (x._4.exists(_._2 != vm.vagrant.model.VmStatus.running)) {
      log.debug("vm not provisioned")
      log.debug(x._2)
      vmProvisioned = false
    } else {
      log.debug("vm provisioned")
      log.debug(x._2)
      vmProvisioned = true
      nodeActor ! VmProvisioned
      vmProxyActor ! SetVagrantEnvironmentConfig(vagrantEnvironmentConfig)
    }
  }

  private def registerScheduler = {
    log.debug(s"got NoDeployInfo")
    if (cancellable != null) cancellable.cancel()
    cancellable = context.system.scheduler.schedule(10 seconds, 60 seconds, instanceActor, GetDeployInfo)(context.dispatcher, self)
  }

  private def tasksDone = {
    val runnable: Runnable = () => {
      val vmActor = self
      vagrantEnvironment.destroy()
      if(path != null)
        sbt.io.IO.delete(path)
      vmActor ! VmTaskResult(VmDestroy())
    }
    val vmActorHelper: ActorRef = context.actorOf(Props[VMActorHelper], s"vmActorHelper_${new Random().nextLong()}")
    vmActorHelper ! VmTask(runnable)
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    if (vagrantEnvironment != null)
      vagrantEnvironment.destroy()
    if (path != null)
      sbt.io.IO.delete(path)
    log.debug(s"goodbye from ${self.path.name}")
  }
}
