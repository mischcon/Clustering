package vm

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import utils.messages.SystemAttributes
import vm.messages._
import worker.messages.{DeployInfo, GetDeployInfo, NoDeployInfo}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
  * Created by mischcon on 3/20/17.
  */
class NodeActor extends Actor with ActorLogging {

  private var nodeMasterActor: ActorRef = _
  private var nodeMonitorActor: ActorRef = _
  private var globalStatusActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var vmActors: Map[String, (ActorRef, ActorRef)] = Map()

  self ! Init

  override def receive: Receive = {
    case Init => init
    case SetGlobalStatusActor(globalStatusActor) => this.globalStatusActor = globalStatusActor
    case SetInstanceActor(instanceActor) => this.instanceActor = instanceActor
    case GetInstanceActor => sender ! SetInstanceActor(instanceActor)
    case GetVmProxyActor => sender() ! getVmProxyActor(sender().path.name.split("_"){-1})
    case GetVmActor => sender() ! getVmActor(sender().path.name.split("_"){-1})
    case AddVmActor => addVmActor
    case VmProvisioned => ??? //ToDo: Scheduler registerien und nach 15 Sek AddVmActor Aufrufen
    case GetGlobalStatusActor => sender() ! SetGlobalStatusActor(globalStatusActor)
  }

  private def init = {
    nodeMasterActor = context.parent
    nodeMasterActor ! GetInstanceActor
    nodeMasterActor ! GetGlobalStatusActor
    nodeMonitorActor = context.actorOf(Props[NodeMonitorActor], "nodeMonitorActor")
    self ! AddVmActor
  }

  private def addVmActor = {
    implicit val timeout = Timeout(20 seconds)
    val systemAttributesFuture = nodeMonitorActor ? GetSystemAttributes
    val systemAttributes = Await.result(systemAttributesFuture, timeout.duration).asInstanceOf[SystemAttributes].attributes
    val vagrantEnvironmentConfigFuture = instanceActor ? GetDeployInfo
    var memory = 0
    Await.result(vagrantEnvironmentConfigFuture, timeout.duration) match {
      case DeployInfo(vagrantEnvironmentConfig) => memory = vagrantEnvironmentConfig.vmConfigs().asScala.map(_.provider().memory().intValue()).sum
      case NoDeployInfo => memory = -1
    }
    if (memory > 0 && systemAttributes {"FreePhysicalMemorySize"}.toLong >= memory) {
      val uuid = UUID.randomUUID().toString
      val vmProxyActor = context.actorOf(Props[VMProxyActor], s"vmProxyActor_$uuid")
      val vmActor = context.actorOf(Props[VMActor], s"vmActor_$uuid")
      vmActors += uuid -> (vmActor, vmProxyActor)
    }
  }

  private def getVmActor(uuid: String): SetVmActor = {
    if (vmActors.contains(uuid)) {
      SetVmActor(vmActors{uuid}._1)
    }
    SetVmActor(null)
  }

  private def getVmProxyActor(uuid: String): SetVmProxyActor = {
    if (vmActors.contains(uuid)) {
      SetVmProxyActor(vmActors{uuid}._2)
    }
    SetVmProxyActor(null)
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    nodeMasterActor ! DeregisterNodeActor
    log.debug(s"goodbye from ${self.path.name}")
  }

}
