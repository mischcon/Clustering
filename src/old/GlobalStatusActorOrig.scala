package utils

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}
import utils.messages.{DeregisterNodeMonitorActor, RegisterNodeMonitorActor, SystemAttributes}
import vm.messages.GetSystemAttributes

import scala.concurrent.duration._


/**
  * Created by mischcon on 3/20/17.
  */
class GlobalStatusActorOrig extends Actor with ActorLogging {
  private var nodeMonitorActors: Map[ActorRef, Cancellable] = Map()

  override def receive: Receive = {
    case RegisterNodeMonitorActor => registerNodeMonitorActor(sender())
    case DeregisterNodeMonitorActor => deregisterNodeMonitorActor(sender())
    case SystemAttributes(attributes) => log.debug(s"got system Attributes from ${sender().path}")
  }

  private def registerNodeMonitorActor(nodeMonitorActor: ActorRef) = {
    log.debug(s"actor ${nodeMonitorActor.path} registered")
    val cancellable = context.system.scheduler.schedule(5 seconds, 60 seconds, nodeMonitorActor, GetSystemAttributes)(context.dispatcher, self)
    nodeMonitorActors += nodeMonitorActor -> cancellable
  }

  private def deregisterNodeMonitorActor(nodeMonitorActor: ActorRef) = {
    if (nodeMonitorActors.contains(nodeMonitorActor)) {
      log.debug(s"actor ${nodeMonitorActor.path} deregistered")
      val cancellable = nodeMonitorActors{nodeMonitorActor}
      cancellable.cancel()
      nodeMonitorActors -= nodeMonitorActor
    }
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }
}