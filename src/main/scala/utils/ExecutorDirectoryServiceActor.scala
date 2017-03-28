package utils

import akka.actor.{Actor, ActorLogging, Address}
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberExited, MemberJoined}
import akka.cluster.{Cluster, Member}
import utils.messages.{ExecutorAddress, GetExecutorAddress}

import scala.util.Random

class ExecutorDirectoryServiceActor extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  var directory : Map[Member, Object] = Map.empty

  override def receive: Receive = {
    case MemberJoined(member) => {
      directory += (member -> null)
    }
    case MemberExited(member) => {
      directory -= member
    }
    case GetExecutorAddress => getMember()
  }

  def getMember() = {
    /* until a heal status is available we simply use a random approach */
    sender() ! ExecutorAddress(new Random().shuffle(directory).head._1.address)
  }

  override def preStart(): Unit = {
    super.preStart()
    log.debug("Hello from ExecutorDirectoryService")
    log.debug("Now reacting on MemberJoined and MemberExited Cluster Events")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberJoined], classOf[MemberExited])
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug("Goodbye from ExecutorDirectoryService")
    cluster.unsubscribe(self)
  }
}
