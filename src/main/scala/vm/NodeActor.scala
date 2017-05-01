package vm

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import vm.messages._
import worker.messages.{DeployInfo, GetDeployInfo, NoDeployInfo}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
  * Created by mischcon on 3/20/17.
  */
class NodeActor extends Actor with ActorLogging {

  private var nodeMasterActor: ActorRef = context.parent
  private var nodeMonitorActor: ActorRef = _
  private var globalStatusActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var distributorActor: ActorRef = _
  private var vmActors: Map[String, (ActorRef, ActorRef)] = Map()
  init

  override def receive: Receive = {
    case SetGlobalStatusActor(globalStatusActor) => this.globalStatusActor = globalStatusActor
    case SetInstanceActor(instanceActor) => this.instanceActor = instanceActor
    case SetDistributorActor(distributorActor) => this.distributorActor = distributorActor
    case GetInstanceActor => sender ! SetInstanceActor(instanceActor)
    case GetVmProxyActor => sender() ! getVmProxyActor(sender().path.name.split("-"){-1})
    case GetVmActor => sender() ! getVmActor(sender().path.name.split("-"){-1})
    case GetDistributorActor => sender() ! SetDistributorActor(distributorActor)
    case VmProvisioned => ???
  }

  private def init = {
    nodeMasterActor ! GetInstanceActor
    nodeMasterActor ! GetGlobalStatusActor
    nodeMasterActor ! GetDistributorActor
    nodeMonitorActor = context.actorOf(Props[NodeMonitorActor], "nodeMonitorActor")
    addVmActor
  }

  private def addVmActor = {
    implicit val timeout = Timeout(5 seconds)
    val systemAttributesFuture = nodeMonitorActor ? GetSystemAttributes
    val systemAttributes = Await.result(systemAttributesFuture, timeout.duration).asInstanceOf[Map[String, String]]
    val vagrantEnvironmentConfigFuture = instanceActor ? GetDeployInfo
    var memory = 0
    Await.result(vagrantEnvironmentConfigFuture, timeout.duration) match {
      case DeployInfo(vagrantEnvironmentConfig) => memory = vagrantEnvironmentConfig.vmConfigs().asScala.map(_.provider().memory().intValue()).sum
      case NoDeployInfo => memory = -1
    }
    if (memory > 0 && systemAttributes {"FreePhysicalMemorySize"}.toInt >= memory) {
      val uuid = UUID.randomUUID().toString
      val vmProxyActor = context.actorOf(Props[VMProxyActor], s"vmProxyActor-$uuid")
      val vmActor = context.actorOf(Props[VMActor], s"vmActor-$uuid")
      vmActors +=  uuid -> (vmActor, vmProxyActor)
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
