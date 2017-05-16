package utils.messages

import akka.actor.{ActorRef, Address}

trait GlobalStatusMessage

case class RegisterNodeMonitorActor(actor: ActorRef) extends GlobalStatusMessage

case class DeregisterNodeMonitorActor(actor: ActorRef) extends GlobalStatusMessage

case class RegisterVmActor(address: Address) extends GlobalStatusMessage

case class DeregisterVmActor(address: Address) extends GlobalStatusMessage

case class SystemAttributes(attributes: Map[String, String]) extends GlobalStatusMessage

case class GlobalSystemAttributes(globalAttributes : Map[ActorRef, Map[String, String]]) extends GlobalStatusMessage

case object GetGlobalSystemAttributes extends GlobalStatusMessage

case object GetVMInfos extends GlobalStatusMessage

case class VMInfos(infos: Map[Address, Int]) extends GlobalStatusMessage