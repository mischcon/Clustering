package vm

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import vm.messages._
import vm.vagrant.configuration.VagrantEnvironmentConfig

/**
  * Created by mischcon on 3/20/17.
  */
class NodeActor extends Actor with ActorLogging {

  private var master: String = _
  private var vmEnvironment: VagrantEnvironmentConfig = _
  private var nodeMonitorActor: ActorRef = _
  private var vmActor: ActorRef = _
  private var vmProxyActor: ActorRef = _

  override def receive: Receive = {
    case SetMaster(master) => this.master = master; startMonitoring;
    case SetVmEnvironment(vmEnvironment) => this.vmEnvironment = vmEnvironment; setMonitorPath; startVmEnvironment
    case StopMonitoring => stopMonitoring
    case StartMonitoring => startMonitoring
  }

  private def startMonitoring = {
    if (nodeMonitorActor == null)
      nodeMonitorActor = context.actorOf(Props[NodeMonitorActor], "nodeMonitorActor")
    nodeMonitorActor ! SetMaster(master)
    if (vmEnvironment != null)
      nodeMonitorActor ! SetPath(vmEnvironment.path())
  }

  private def setMonitorPath = {
    if (nodeMonitorActor != null)
      nodeMonitorActor ! SetPath(vmEnvironment.path())
  }

  private def stopMonitoring = {
    if (nodeMonitorActor != null)
      nodeMonitorActor ! StopMonitoring
  }

  private def startVmEnvironment = {
    if (vmActor == null)
      vmActor = context.actorOf(Props[VMActor], "vmActor")
    vmActor ! SetVmEnvironment(vmEnvironment)
    if (nodeMonitorActor != null)
      vmActor ! SetNodeMonitorActor(nodeMonitorActor)
  }


}
