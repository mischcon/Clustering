package vm

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Address, Deploy, Props, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberJoined}
import akka.remote.RemoteScope
import vm.messages._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
  * Created by oliver.ziegert on 22.04.17.
  */
class NodeMasterActor extends Actor with ActorLogging {


  private var globalStatusActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var nodeActors: List[ActorRef] = _
  private var cluster: Cluster = _
  private var ready : Boolean = _
  private val globalStatusActorPath = "/user/globalStatus"
  private val instanceActorPath = "/user/instances"

  self ! Init

  override def receive: Receive = {
    case Init                                  => log.debug("got Init");                          handlerInit
    case SetInstanceActor(actor)               => log.debug(s"got SetInstanceActor($actor)");     handlerSetInstanceActor(actor)
    case SetGlobalStatusActor(actor)           => log.debug(s"got SetGlobalStatusActor($actor)"); handlerSetGlobalStatusActor(actor)
    case NotReadyJet(message)                  => log.debug(s"got NotReadyJet($message)");        handlerNotReadyJet(message)
    case GetGlobalStatusActor        if ready  => log.debug("got GetGlobalStatusActor");          handlerGetGlobalStatusActor
    case GetInstanceActor            if ready  => log.debug("got GetInstanceActor");              handlerGetInstanceActor
    case DeregisterNodeActor(actor)  if ready  => log.debug("got DeregisterNodeActor");           handlerDeregisterNodeActor(actor)
    case IncludeNode(address)        if ready  => log.debug(s"got IncludeNode($address)");        handlerIncludeNode(address)
    case MemberJoined(member)        if ready  => log.debug(s"got MemberJoined($member)");        handlerIncludeNode(member.address)
    case Terminated(actor)                     => log.debug(s"got Terminated($actor)");           handlerDeregisterNodeActor(actor)
    case x: Any                      if !ready => log.debug("got Message but NotReadyJet");       handlerNotReady(x)
  }

  private def handlerInit = {
    ready = false
    nodeActors = List()
    cluster = Cluster(context.system)
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberJoined])
    context.actorSelection(globalStatusActorPath) ! GetGlobalStatusActor
    context.actorSelection(instanceActorPath) ! GetInstanceActor
  }

  private def handlerSetInstanceActor(instanceActor: ActorRef) = {
    this.instanceActor = instanceActor
    if (globalStatusActor != null && instanceActor != null) ready = true
  }

  private def handlerSetGlobalStatusActor(globalStatusActor: ActorRef) = {
    this.globalStatusActor = globalStatusActor
    if (globalStatusActor != null && instanceActor != null) ready = true
  }

  private def handlerDeregisterNodeActor(actorRef: ActorRef) = {
    if (nodeActors.contains(actorRef))
      nodeActors = nodeActors.diff(actorRef :: Nil)
  }

  private def handlerIncludeNode(address: Address) = {
    val actorName = s"nodeActor_${address.host.getOrElse(UUID.randomUUID().toString)}:${address.port.getOrElse("")}"
    nodeActors +:= context.actorOf(Props[NodeActor].withDeploy(Deploy(scope = RemoteScope(address))), actorName)
  }

  private def handlerGetGlobalStatusActor = {
    sender() ! SetGlobalStatusActor(globalStatusActor)
  }

  private def handlerGetInstanceActor = {
    sender() ! SetInstanceActor(instanceActor)
  }

  private def handlerNotReady(any: Any) = {
    sender() ! NotReadyJet(any)
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
    log.debug(s"goodbye from ${self.path.name}")
    if (cluster != null)
      cluster.unsubscribe(self)
  }
}
