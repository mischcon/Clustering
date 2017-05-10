package utils.messages

import akka.actor.ActorRef

/**
  * Created by mischcon on 3/20/17.
  */
trait GlobalStatusMessage

case class RegisterNodeMonitorActor(actor: ActorRef) extends GlobalStatusMessage

case class DeregisterNodeMonitorActor(actor: ActorRef) extends GlobalStatusMessage

case class SystemAttributes(attributes: Map[String, String]) extends GlobalStatusMessage