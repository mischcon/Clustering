package vm


import java.io.File
import java.util.{Random, UUID}

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import com.rits.cloning.Cloner
import utils.DeployInfoInterface
import utils.messages.{DeregisterVmActor, RegisterVmActor, SystemAttributes}
import vm.messages._
import vm.vagrant.Vagrant
import vm.vagrant.configuration.VagrantEnvironmentConfig
import vm.vagrant.model.VagrantEnvironment
import vm.vagrant.util.VagrantException
import worker.messages._

import scala.collection.JavaConverters.{asJavaIterableConverter, iterableAsScalaIterableConverter}
import scala.concurrent.duration.{DurationInt, FiniteDuration}


/**
  * Created by oliver.ziegert on 3/20/17.
  */
class VMActor extends Actor with ActorLogging {

  private var uuid: String = _
  private var nodeActor: ActorRef = _
  private var nodeMonitorActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var globalStatusActor: ActorRef = _
  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _
  private var vagrantEnvironment: VagrantEnvironment = _
  private var vmProxyActor: ActorRef = _
  private var vmProvisioned: Boolean = _
  private var scheduleOnceGetDeployInfo: Cancellable = _
  private var systemAttributes: Map[String, String] = _
  private var vagrant: Boolean = _
  private var path: File = _
  private var ready: Boolean = _

  self ! Init

  override def receive: Receive = {
    case Init                                                       => log.debug("got Init");                           handlerInit
    case SetInstanceActor(actor)                                    => log.debug(s"got SetInstanceActor($actor)");      handlerSetInstanceActor(actor)
    case SetVmProxyActor(actor)                                     => log.debug(s"got SetVmProxyActor($actor)");       handlerSetVmProxyActor(actor)
    case SetNodeMonitorActor(actor)                                 => log.debug(s"got SetNodeMonitorActor($actor)");   handlerSetNodeMonitorActor(actor)
    case SetGlobalStatusActor(actor)                                => log.debug(s"got SetGlobalStatusActor($actor)");  handlerSetGlobalStatusActor(actor)
    case NotReadyJet(message)                                       => log.debug(s"got NotReadyJet($message)");         handlerNotReadyJet(message)
    case DeployInfo(config)           if ready                      => log.debug(s"got DeployInfo($config)");           handlerDeployInfo(config)
    case SystemAttributes(attributes) if ready                      => log.debug(s"got SystemAttributes($attributes)"); handlerSystemAttributes(attributes)
    case NoDeployInfo                 if ready                      => log.debug("got NoDeployInfo");                   handlerNoDeployInfo
    case VmTaskResult(any)            if ready                      => log.debug(s"got VmTaskResult($any)");            handlerVmTaskResult(any)
    case NoMoreTasks                  if vmProvisioned              => log.debug("got NoMoreTasks");                    handlerNoMoreTasks
    case GetVagrantEnvironmentConfig  if vmProvisioned              => log.debug("got GetVagrantEnvironmentConfig");    handlerGetVagrantEnvironmentConfig
    case x: Any                       if (!ready || !vmProvisioned) => log.debug(s"got Message $x but NotReadyJet");    handlerNotReady(x)
  }

  private def handlerInit = {
    uuid = self.path.name.split("_"){1}
    nodeActor = context.parent
    vagrant = false
    vmProvisioned = false
    ready = false
    nodeActor ! GetInstanceActor
    nodeActor ! GetVmProxyActor(self)
    nodeActor ! GetNodeMonitorActor
    nodeActor ! GetGlobalStatusActor
  }

  private def handlerSetInstanceActor(actor: ActorRef) = {
    this.instanceActor = actor
    checkReady
  }

  private def handlerSetVmProxyActor(actor: ActorRef) = {
    this.vmProxyActor = actor
    checkReady
  }

  private def handlerSetNodeMonitorActor(actor: ActorRef) = {
    this.nodeMonitorActor = actor
    checkReady
  }

  private def handlerSetGlobalStatusActor(actor: ActorRef) = {
    this.globalStatusActor = actor
    checkReady
  }

  private def handlerGetVagrantEnvironmentConfig = {
    sender() ! SetVagrantEnvironmentConfig(vagrantEnvironmentConfig)
  }

  private def handlerDeployInfo[T >: DeployInfoInterface](deployInfo: T): Unit = {
    scheduleOnceGetDeployInfo = null
    deployInfo match {
      case config: VagrantEnvironmentConfig => handlerDeployInfo(config)
    }
  }

  private def handlerNoDeployInfo = {
    this.scheduleOnceGetDeployInfo = null
    this.vagrantEnvironmentConfig = null
    if (systemAttributes != null && vagrant)
      scheduleOnceGetDeployInfo(30 seconds)
  }

  private def handlerDeployInfo(vagrantEnvironmentConfig: VagrantEnvironmentConfig) = {
    this.vagrantEnvironmentConfig = new Cloner().deepClone(vagrantEnvironmentConfig)
    prepareProvisionVm
  }

  private def handlerSystemAttributes(attributes: Map[String, String]) = {
    systemAttributes = attributes
    vagrant = attributes{"Vagrant"}.toBoolean
    prepareProvisionVm
  }

  private def handlerNotReady(any: Any) = {
    sender() ! NotReadyJet(any)
  }

  private def handlerNotReadyJet(any: Any) = {
    scheduleOnceRetry(5 seconds, sender(), any)
  }

  private def handlerVmTaskResult(any: Any) = {
    any match {
      case x: (VagrantEnvironment, String, VagrantEnvironmentConfig, Iterator[(String, vm.vagrant.model.VmStatus.Value)]) => finishProvisionVm(x)
      case x: VmDestroy => nodeActor ! RemoveVmActor(self); globalStatusActor ! DeregisterVmActor(self.path.address); context.stop(self)
    }
  }

  private def handlerNoMoreTasks = {
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

  private def checkReady = {
    if (instanceActor != null && vmProxyActor != null && nodeMonitorActor != null && globalStatusActor != null) {
      ready = true
      if (scheduleOnceGetDeployInfo == null)
        scheduleOnceGetDeployInfo(0 seconds)
      nodeMonitorActor ! GetSystemAttributes
    }
  }

  private def prepareProvisionVm = {
    if (vagrant && vagrantEnvironmentConfig != null) {
      vagrantEnvironmentConfig.path().mkdirs()
      if (!vagrantEnvironmentConfig.path().isDirectory)
        throw new VagrantException("path is not a directory!")
      if (path != null)
        sbt.io.IO.delete(path)
      path = new File(vagrantEnvironmentConfig.path(), uuid)
      val version = vagrantEnvironmentConfig.version()
      path.mkdir()
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
        d.setName(d.provider().vmName())
      })

      val runnable: Runnable = () => {
        val vmActor = self
        vagrantEnvironment = new Vagrant().createEnvironment(vagrantEnvironmentConfig)
        var output = ""
        try {
          output = vagrantEnvironment.destroy()
          output += s"\n${vagrantEnvironment.updateBoxes()}"
          output += s"\n${vagrantEnvironment.up()}"
          val vmConfigs = vagrantEnvironmentConfig.vmConfigs().asScala.map(vagrantEnvironment.getBoxePortMapping(_))
          vagrantEnvironmentConfig = new VagrantEnvironmentConfig(vmConfigs.asJava, vagrantEnvironmentConfig.path())
          vagrantEnvironmentConfig.setVersion(version)
          vmActor ! VmTaskResult((vagrantEnvironment, output, vagrantEnvironmentConfig, vagrantEnvironment.status()))
        } catch {
          case e: Exception => {
            log.debug(s"vagrant up faild: $e\n ${e.getStackTrace.mkString("\n")}")
            vagrantEnvironment.destroy()
            sbt.io.IO.delete(path)
            instanceActor.tell(GetDeployInfo, vmActor)
          }
        }
      }
      val vmActorHelper: ActorRef = context.actorOf(Props[VMActorHelper], s"vmActorHelper_${UUID.randomUUID().toString}")
      vmActorHelper ! VmTask(runnable)
    }
  }

  private def finishProvisionVm(x: (VagrantEnvironment, String, VagrantEnvironmentConfig, Iterator[(String, vm.vagrant.model.VmStatus.Value)])) = {
    if (x._4.exists(_._2 != vm.vagrant.model.VmStatus.running)) {
      log.debug("vm not provisioned")
      log.debug(x._2)
      vmProvisioned = false
      prepareProvisionVm
    } else {
      log.debug("vm provisioned")
      log.debug(x._2)
      vmProvisioned = true
      nodeActor ! VmProvisioned
      vmProxyActor ! SetVagrantEnvironmentConfig(vagrantEnvironmentConfig)
      globalStatusActor ! RegisterVmActor(self.path.address)
    }
  }

  private def scheduleOnceGetDeployInfo(delay: FiniteDuration): Unit = {
    scheduleOnceGetDeployInfo = context.system.scheduler.scheduleOnce(delay, instanceActor, GetDeployInfo)(context.dispatcher, self)
  }

  private def scheduleOnceRetry(delay: FiniteDuration, receiver: ActorRef, message: Any) = {
    context.system.scheduler.scheduleOnce(delay, receiver, message)(context.dispatcher, self)
  }

  override def preStart = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop = {
    if (vagrantEnvironment != null)
      vagrantEnvironment.destroy()
    if (path != null)
      sbt.io.IO.delete(path)
    log.debug(s"goodbye from ${self.path.name}")
  }
}
