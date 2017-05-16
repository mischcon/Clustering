package de.oth.clustering.scala.utils.messages

import akka.actor.{ActorRef, Address}

trait GlobalStatusMessage

case class RegisterNodeMonitorActor(actor: ActorRef) extends GlobalStatusMessage

case class DeregisterNodeMonitorActor(actor: ActorRef) extends GlobalStatusMessage

case class RegisterVmActor(address: String) extends GlobalStatusMessage

case class DeregisterVmActor(address: String) extends GlobalStatusMessage

case class SystemAttributes(attributes: Map[String, String]) extends GlobalStatusMessage

case class GlobalSystemAttributes(globalAttributes : Map[ActorRef, Map[String, String]]) extends GlobalStatusMessage

case object GetGlobalSystemAttributes extends GlobalStatusMessage

case object GetVMInfos extends GlobalStatusMessage

case class VMInfos(infos: Map[String, Int]) extends GlobalStatusMessage