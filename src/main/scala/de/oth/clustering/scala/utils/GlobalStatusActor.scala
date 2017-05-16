package de.oth.clustering.scala.utils

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Terminated}
import de.oth.clustering.scala.utils.messages._
import de.oth.clustering.scala.vm.messages.{GetGlobalStatusActor, GetSystemAttributes, NotReadyJet, SetGlobalStatusActor}

import scala.concurrent.duration._


/**
  * Created by oliver.ziegert on 3/20/17.
  */
class GlobalStatusActor extends Actor with ActorLogging {

  private var nodeMonitorActors: Map[ActorRef, Cancellable] = Map()
  private var nodeAttributes: Map[ActorRef, Map[String, String]] = Map()
  private var nodeVms: Map[String, Int] = Map()

  override def receive: Receive = {
    case RegisterNodeMonitorActor(actor)   => log.debug(s"got RegisterNodeMonitorActor($actor)");   handlerRegisterNodeMonitorActor(actor)
    case RegisterVmActor(address)          => log.debug(s"got RegisterVmActor($address)");          handlerRegisterVmActor(address)
    case DeregisterNodeMonitorActor(actor) => log.debug(s"got DeregisterNodeMonitorActor($actor)"); handlerDeregisterNodeMonitorActor(actor)
    case DeregisterVmActor(address)        => log.debug(s"got DeregisterVmActor($address)");        handlerDeregisterVmActor(address)
    case SystemAttributes(attributes)      => log.debug("got SystemAttributes");                    handlerSystemAttributes(attributes)
    case Terminated(actor)                 => log.debug(s"got Terminated($actor)");                 handlerDeregisterNodeMonitorActor(actor)
    case GetGlobalStatusActor              => log.debug(s"got GetGlobalStatusActor");               handlerGetGlobalStatusActor
    case GetGlobalSystemAttributes         => log.debug("got GetGlobalSystemAttributes");           handlerGetGlobalSystemAttributes
    case GetVMInfos                        => log.debug("got GetVmInfos");                          handlerGetVMInfo
    case NotReadyJet(message)              => log.debug(s"got NotReadyJet($message)");              handlerNotReadyJet(message)
  }

  private def handlerRegisterNodeMonitorActor(nodeMonitorActor: ActorRef) = {
    log.info(s"actor ${nodeMonitorActor.path} registered")
    context.watch(nodeMonitorActor)
    val cancellable = context.system.scheduler.schedule(5 seconds,
      5 seconds,
      nodeMonitorActor,
      GetSystemAttributes)(
      context.dispatcher,
      self)
      nodeMonitorActors += nodeMonitorActor -> cancellable
  }

  private def handlerDeregisterNodeMonitorActor(nodeMonitorActor: ActorRef) = {
    log.info(s"actor ${nodeMonitorActor.path} deregistered")
    if (nodeMonitorActors.contains(nodeMonitorActor)) {
      log.debug(s"actor ${nodeMonitorActor.path} found")
      val cancellable = nodeMonitorActors{nodeMonitorActor}
      cancellable.cancel()
      nodeMonitorActors -= nodeMonitorActor
      nodeAttributes -= nodeMonitorActor
    }
  }

  private def handlerRegisterVmActor(address: String) = {
    log.info(s"vmActor ${address} registered")
    if (nodeVms.contains(address)) {
      log.debug(s"vmActor ${address} found")
      var countVms = nodeVms{address}
      countVms += 1
      nodeVms -= address
      nodeVms += address -> countVms
      log.debug(s"vmActor ${address} increased")
    } else {
      nodeVms += address -> 1
      log.debug(s"vmActor $address added to Map")
    }
  }

  private def handlerDeregisterVmActor(address: String) = {
    if (nodeVms.contains(address)) {
      log.debug(s"vmActor ${address} found")
      var countVms = nodeVms{address}
      if (countVms > 0 ) {
        countVms -= 1
        nodeVms -= address
        nodeVms += address -> countVms
        log.debug(s"vmActor ${address} decreased")
      } else {
        log.debug("can not decrease de.oth.de.oth.clustering.java.clustering.scala.vm Counter!!!")
        log.debug(s"vmActor ${address} countVm: $countVms")
      }
    }
  }

  private def handlerGetGlobalStatusActor = {
    sender() ! SetGlobalStatusActor(self)
  }

  private def handlerGetGlobalSystemAttributes = {
    sender() ! GlobalSystemAttributes(nodeAttributes)
  }

  private def handlerGetVMInfo = {
    sender() ! VMInfos(nodeVms)
  }

  private def handlerSystemAttributes(attributes: Map[String, String]) = {
    log.debug(s"got system Attributes from ${sender().path}")
    nodeAttributes += (sender() -> attributes)
  }

  private def handlerNotReadyJet(any: Any) = {
    scheduleOnceRetry(5 seconds, sender(), any)
  }

  private def scheduleOnceRetry(delay: FiniteDuration, receive: ActorRef, message: Any) = {
    context.system.scheduler.scheduleOnce(delay, receive, message)(context.dispatcher, self)
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    nodeMonitorActors.foreach(d => if (!d._2.isCancelled) d._2.cancel())
    log.debug(s"goodbye from ${self.path.name}")
  }
}
