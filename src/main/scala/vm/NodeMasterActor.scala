package vm

import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, Address, Deploy, Props}
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberJoined, UnreachableMember}
import akka.remote.RemoteScope
import vm.messages._

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

/**
  * Created by oliver.ziegert on 22.04.17.
  */
class NodeMasterActor extends Actor with ActorLogging {

  private var globalStatusActor: ActorRef = getActor("/user/globalStatus").orNull
  private var instanceActor: ActorRef = getActor("/user/instances").orNull
  //private var distributorActor: ActorRef = getActor("/user/distributor").orNull
  private var nodeActors: List[ActorRef] = List()
  private val cluster = Cluster(context.system)

  override def receive: Receive = {
    case GetGlobalStatusActor => sender() ! SetGlobalStatusActor(globalStatusActor)
    case GetInstanceActor => sender() ! SetInstanceActor(instanceActor)
    //case GetDistributorActor => sender() ! SetDistributorActor(distributorActor)
    case DeregisterNodeActor => nodeActors = nodeActors.diff(sender() :: Nil)
    case IncludeNode(address) => includeNode(address)
    case MemberJoined(member) => handleMemberJoined(member)
  }

  def includeNode(address: Address) = {
    nodeActors +:= context.actorOf(Props[NodeActor].withDeploy(Deploy(scope = RemoteScope(address))), "nodeActor")
  }

  private def getActor(path: String): Option[ActorRef] = {
    import context.dispatcher
    context.actorSelection(path).resolveOne(1 second).onComplete {
      case Success(actorRef) => Some(actorRef)
      case Failure(ex) => log.warning(s"$path does not exist"); None
    }
    None
  }

  def handleMemberJoined(member : Member) = {
    if(member.hasRole("vm")){
      includeNode(member.address)
    }
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
