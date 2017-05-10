package vm

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Address, Deploy, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberJoined}
import akka.remote.RemoteScope
import akka.util.Timeout
import vm.messages._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
  * Created by oliver.ziegert on 22.04.17.
  */
class NodeMasterActorOrig extends Actor with ActorLogging {


  private var globalStatusActor: ActorRef = _
  private var instanceActor: ActorRef = _
  private var nodeActors: List[ActorRef] = List()
  private val cluster = Cluster(context.system)

  self ! Init

  override def receive: Receive = {
    case Init => init
    case GetGlobalStatusActor => sender() ! SetGlobalStatusActor(globalStatusActor)
    case GetInstanceActor => sender() ! SetInstanceActor(instanceActor)
    case DeregisterNodeActor => excludeNode
    case IncludeNode(address) => log.debug(s"got IncludeNode($address)"); includeNode(address)
    case MemberJoined(member) => log.debug(s"got MemberJoined($member)"); includeNode(member.address)
  }

  private def init = {
    globalStatusActor = getActor("/user/globalStatus").orNull
    instanceActor = getActor("/user/instances").orNull
  }

  def includeNode(address: Address) = {
    nodeActors +:= context.actorOf(Props[NodeActor].withDeploy(Deploy(scope = RemoteScope(address))), s"nodeActor_${address.host.getOrElse(UUID.randomUUID().toString)}:${address.port.getOrElse("")}")
  }

  def excludeNode = {
    nodeActors = nodeActors.diff(sender() :: Nil)
  }

  private def getActor(path: String): Option[ActorRef] = {
    implicit val timeout = Timeout(5 seconds)
    val actorFuture = context.actorSelection(path).resolveOne
    Await.result(actorFuture, timeout.duration) match {
      case x: ActorRef => return Some(x)
      case _ => log.warning(s"$path does not exist"); return None
    }
    log.debug(s"could not find $path")
    return None
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberJoined])
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
    cluster.unsubscribe(self)
  }
}
