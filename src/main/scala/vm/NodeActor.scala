package vm

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.pattern._
import akka.util.Timeout
import utils.messages.SystemAttributes
import vm.messages._
import vm.vagrant.configuration.{VagrantEnvironmentConfig, VagrantPortForwardingConfig, VagrantProviderConfig}
import worker.messages.{DeployInfo, GetDeployInfo, NoDeployInfo}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.{Await, JavaConversions}
import scala.concurrent.duration.DurationInt

/**
  * Created by mischcon on 3/20/17.
  */
class NodeActor extends Actor with ActorLogging {

  private var nodeMasterActor: ActorRef = _
  private var nodeMonitorActor: ActorRef = _
  private var globalStatusActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var systemAttributes: Map[String,String] = _
  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _
  private var cancellableGetDeployInfo: Cancellable = _
  private var cancellableAddVmActor: Cancellable = _
  private var vmActors: Map[String, (ActorRef, ActorRef)] = Map()

  self ! Init

  override def receive: Receive = {
    case Init => init
    case SetGlobalStatusActor(globalStatusActor) => log.debug("got SetGlobalStatusActor"); this.globalStatusActor = globalStatusActor; initNodeMonitorActor
    case SetInstanceActor(instanceActor) => log.debug("got SetInstanceActor"); this.instanceActor = instanceActor; initNodeMonitorActor
    case GetInstanceActor => log.debug("got GetInstanceActor"); sender ! SetInstanceActor(instanceActor)
    case GetVmProxyActor(caller) => log.debug("got GetVmProxyActor"); sender() ! getVmProxyActor(caller.path.name.split("_"){1})
    case GetVmActor(caller) => log.debug("got GetVmActor"); sender() ! getVmActor(caller.path.name.split("_"){1})
    case GetGlobalStatusActor => log.debug("got GetGlobalStatusActor"); sender() ! SetGlobalStatusActor(globalStatusActor)
    case AddVmActor => log.debug("got AddVmActor"); deregisterAddVmActor; addVmActorInit
    case RemoveVmActor(actor) => log.debug(s"got RemoveVmActor from ${actor.path.name}"); removeVm(actor)
    case DeployInfo(vagrantEnvironmentConfig) => log.debug("got DeployInfo"); this.vagrantEnvironmentConfig = vagrantEnvironmentConfig; deregisterGetDeployInfo; addVmActor
    case NoDeployInfo => log.debug("got NoDeployInfo"); this.vagrantEnvironmentConfig == null; registerGetDeployInfo
    case SystemAttributes(attributes) => log.debug("got SystemAttributes"); this.systemAttributes = attributes; addVmActor
    case VmProvisioned => log.debug("got VmProvisioned"); registerAddVmActor
  }

  private def init = {
    nodeMasterActor = context.parent
    nodeMasterActor ! GetInstanceActor
    nodeMasterActor ! GetGlobalStatusActor
  }

  private def initNodeMonitorActor = {
    if (instanceActor != null && globalStatusActor != null && nodeMonitorActor == null) {
      nodeMonitorActor = context.actorOf(Props[NodeMonitorActor], "nodeMonitorActor")
      self ! AddVmActor
    }
  }

  private def addVmActorInit = {
    vagrantEnvironmentConfig = null
    systemAttributes = null
    nodeMonitorActor ! GetSystemAttributes
    instanceActor ! GetDeployInfo
  }

  private def addVmActor = {
    log.debug("addVm called")
    if (vagrantEnvironmentConfig != null && systemAttributes != null){
      nodeMonitorActor ! SetPath(vagrantEnvironmentConfig.path())
      val memory = vagrantEnvironmentConfig.vmConfigs().asScala.map(_.provider().memory().toLong).sum * 1024 * 1024
      log.debug(s"FreePhysicalMemorySize: ${systemAttributes {"FreePhysicalMemorySize"}}, memory: $memory")
      if (memory > 0 && systemAttributes {"FreePhysicalMemorySize"}.toLong >= memory) {
        log.debug("create new vmProxyActor")
        val uuid = UUID.randomUUID().toString
        val vmProxyActor = context.actorOf(Props[VMProxyActor], s"vmProxyActor_$uuid")
        var vmActor: ActorRef = null
        if (systemAttributes{"Vagrant"}.toBoolean) {
          log.debug("create new vmActor")
          vmActor = context.actorOf(Props[VMActor], s"vmActor_$uuid")
        }
        vmActors += uuid -> (vmActor, vmProxyActor)
      } else {
        log.debug(s"can not create new vmActor + vmProxyActor, needed Memory: $memory, freeMemory: ${systemAttributes{"FreePhysicalMemorySize"}}")
        registerAddVmActor
      }
    }
  }

  private def getVmActor(uuid: String): SetVmActor = {
    log.debug(s"searching for uuid: $uuid in:\n${this.vmActors}")
    if (vmActors.contains(uuid)) {
      log.debug("found uuid!")
      return SetVmActor(vmActors{uuid}._1)
    }
    log.debug("did not find uuid :(")
    SetVmActor(null)
  }

  private def getVmProxyActor(uuid: String): SetVmProxyActor = {
    if (vmActors.contains(uuid)) {
      return SetVmProxyActor(vmActors{uuid}._2)
    }
    SetVmProxyActor(null)
  }

  private def registerGetDeployInfo = {
    if (cancellableGetDeployInfo == null) {
      cancellableGetDeployInfo = context.system.scheduler.schedule(15 seconds, 60 seconds, instanceActor, GetDeployInfo)(context.dispatcher, self)
    }
  }

  private def deregisterGetDeployInfo = {
    if (cancellableGetDeployInfo != null) {
      cancellableGetDeployInfo.cancel()
      cancellableGetDeployInfo = null
    }
  }

  private def registerAddVmActor = {
    if (cancellableAddVmActor == null) {
      cancellableAddVmActor = context.system.scheduler.schedule(30 seconds, 30 seconds, self, AddVmActor)(context.dispatcher, self)
    }
  }

  private def deregisterAddVmActor = {
    if (cancellableAddVmActor != null) {
      cancellableAddVmActor.cancel()
      cancellableAddVmActor = null
    }
  }

  private def removeVm(actor: ActorRef) = {
    val uuid = actor.path.name.split("_"){1}
    val actorType = actor.path.name.split("_"){0}
    if (vmActors.contains(uuid)) {
      val entry = vmActors{uuid}
      if (actorType.equals("vmProxyActor")) {
        if (entry._1 == null)
          vmActors -= (uuid)
        else {
          vmActors -= (uuid)
          vmActors += uuid -> (entry._1, null)
        }
      } else if (actorType.equals("vmActor")) {
        if (entry._2 == null)
          vmActors -= (uuid)
        else {
          vmActors -= (uuid)
          vmActors += uuid -> (null, entry._2)
        }
      }
    }
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    nodeMasterActor ! DeregisterNodeActor
    log.debug(s"goodbye from ${self.path.name}")
  }

}
