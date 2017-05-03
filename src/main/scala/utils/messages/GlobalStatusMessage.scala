package utils.messages

/**
  * Created by mischcon on 3/20/17.
  */
trait GlobalStatusMessage

case object RegisterNodeMonitorActor extends GlobalStatusMessage

case object DeregisterNodeMonitorActor extends GlobalStatusMessage

case class SystemAttributes(attributes: Map[String, String]) extends GlobalStatusMessage