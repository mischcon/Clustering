package vm

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import utils.messages.SystemAttributes
import vm.messages._
import vm.vagrant.configuration.VagrantEnvironmentConfig
import worker.messages.{DeployInfo, GetDeployInfo, NoDeployInfo}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
  * Created by oliver.ziegert on 3/20/17.
  */
class NodeActor extends Actor with ActorLogging {

  private var nodeMasterActor: ActorRef = _
  private var nodeMonitorActor: ActorRef = _
  private var globalStatusActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var systemAttributes: Map[String,String] = _
  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _
  private var scheduleOnceAddVmActor: Cancellable = _
  private var ready: Boolean = _
  private var vmActors: Map[String, (ActorRef, ActorRef)] = Map()

  self ! Init

  override def receive: Receive = {
    case Init                                   => log.debug("got Init"); handlerInit
    case SetGlobalStatusActor(actor)            => log.debug(s"got SetGlobalStatusActor($actor)");  handlerSetGlobalStatusActor(actor)
    case SetInstanceActor(actor)                => log.debug(s"got SetInstanceActor($actor)");      handlerSetInstanceActor(actor)
    case NotReadyJet(message)                   => log.debug(s"got NotReadyJet($message)");         handlerNotReadyJet(message)
    case GetInstanceActor             if ready  => log.debug("got GetInstanceActor");               handlerGetInstanceActor
    case GetVmProxyActor(actor)       if ready  => log.debug(s"got GetVmProxyActor($actor)");       handlerGetVmProxyActor(actor)
    case GetVmActor(actor)            if ready  => log.debug(s"got GetVmActor($actor)");            handlerGetVmActor(actor)
    case GetGlobalStatusActor         if ready  => log.debug("got GetGlobalStatusActor");           handlerGetGlobalStatusActor
    case AddVmActor                   if ready  => log.debug("got AddVmActor");                     handlerAddVmActor
    case RemoveVmActor(actor)         if ready  => log.debug(s"got RemoveVmActor($actor)");         handlerRemoveVmActor(actor)
    case DeployInfo(deployInfo)       if ready  => log.debug(s"got DeployInfo($deployInfo)");       handlerDeployInfo(deployInfo)
    case NoDeployInfo                 if ready  => log.debug("got NoDeployInfo");                   handlerNoDeployInfo
    case SystemAttributes(attributes) if ready  => log.debug(s"got SystemAttributes($attributes)"); handlerSystemAttributes(attributes)
    case VmProvisioned                if ready  => log.debug("got VmProvisioned");                  handlerVmProvisioned
    case x: _                         if !ready => log.debug(s"got Message but NotReadyJet");       handlerNotReady(x)
  }

  private def handlerInit = {
    nodeMasterActor = context.parent
    ready = false
    nodeMasterActor ! GetInstanceActor
    nodeMasterActor ! GetGlobalStatusActor
  }

  private def handlerSetGlobalStatusActor(actorRef: ActorRef) = {
    this.globalStatusActor = actorRef
    initNodeMonitorActor
  }

  private def handlerSetInstanceActor(actorRef: ActorRef) = {
    this.instanceActor = actorRef
    initNodeMonitorActor
  }

  private def handlerGetInstanceActor = {
    sender() ! SetInstanceActor(instanceActor)
  }

  private def handlerGetVmProxyActor(actorRef: ActorRef) = {
    val uuid = actorRef.path.name.split("_"){1}
    if (vmActors.contains(uuid)) {
      sender() ! SetVmProxyActor(vmActors{uuid}._2)
    }
    sender() ! SetVmProxyActor(null)
  }

  private def handlerGetVmActor(actorRef: ActorRef) = {
    val uuid = actorRef.path.name.split("_"){1}
    if (vmActors.contains(uuid)) {
      sender() ! SetVmProxyActor(vmActors{uuid}._1)
    }
    sender() ! SetVmProxyActor(null)
  }

  private def handlerGetGlobalStatusActor = {
    sender() ! SetGlobalStatusActor(globalStatusActor)
  }

  private def handlerAddVmActor = {
    scheduleOnceAddVmActor = null
    instanceActor ! GetDeployInfo
  }

  private def handlerNoDeployInfo = {
    this.vagrantEnvironmentConfig = null
    if (scheduleOnceAddVmActor == null)
      scheduleOnceAddVmActor(30 seconds)
  }

  private def handlerDeployInfo(vagrantEnvironmentConfig: VagrantEnvironmentConfig) = {
    this.vagrantEnvironmentConfig = vagrantEnvironmentConfig
    nodeMonitorActor ! GetSystemAttributes
  }

  private def handlerSystemAttributes(attributes: Map[String,String]) = {
    this.systemAttributes = attributes
    addVm
  }

  private def handlerRemoveVmActor(actorRef: ActorRef) = {
    val uuid = actorRef.path.name.split("_"){1}
    val actorType = actorRef.path.name.split("_"){0}
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

  private def handlerVmProvisioned = {
    if (scheduleOnceAddVmActor == null)
      scheduleOnceAddVmActor(30 seconds)
  }

  private def handlerNotReady(any: Any) = {
    sender() ! NotReadyJet(any)
  }

  private def handlerNotReadyJet(any: Any) = {
    scheduleOnceRetry(5 seconds, sender(), any)
  }

  private def initNodeMonitorActor = {
    if (instanceActor != null && globalStatusActor != null && nodeMonitorActor == null) {
      ready = true
      nodeMonitorActor = context.actorOf(Props[NodeMonitorActor], "nodeMonitorActor")
      if (scheduleOnceAddVmActor == null)
        scheduleOnceAddVmActor(5 seconds)
    }
  }

  private def addVm = {
    nodeMonitorActor ! SetPath(vagrantEnvironmentConfig.path())
    val memoryNeeded = vagrantEnvironmentConfig.vmConfigs().asScala.map(_.provider().memory().toLong).sum * 1024 * 1024
    val freeMemory = systemAttributes{"FreePhysicalMemorySize"}.toLong
    val vagrant: Boolean = systemAttributes{"Vagrant"}.toBoolean
    if (memoryNeeded > 0 && freeMemory > memoryNeeded && vagrant) {
      val uuid = UUID.randomUUID().toString
      val vmProxyActor = context.actorOf(Props[VMProxyActor], s"vmProxyActor_$uuid")
      val vmActor: ActorRef = context.actorOf(Props[VMActor], s"vmActor_$uuid")
      vmActors += uuid -> (vmActor, vmProxyActor)
    } else {
      log.debug(s"can not create new vmActor & vmProxyActor, needed Memory: $memoryNeeded, freeMemory: ${freeMemory}")
      log.debug(s"vagrant is intalled: $vagrant")
      if (scheduleOnceAddVmActor == null)
        scheduleOnceAddVmActor(30 seconds)
    }
  }

  private def scheduleOnceAddVmActor(delay: FiniteDuration) = {
    scheduleOnceAddVmActor = context.system.scheduler.scheduleOnce(delay, self, AddVmActor)(context.dispatcher, self)
  }

  private def scheduleOnceRetry(delay: FiniteDuration, receive: ActorRef, message: Any) = {
    context.system.scheduler.scheduleOnce(delay, receive, message)(context.dispatcher, self)
  }


  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    nodeMasterActor ! DeregisterNodeActor
    log.debug(s"goodbye from ${self.path.name}")
  }

}
