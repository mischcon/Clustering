package utils

import akka.actor.{Actor, ActorLogging, Address}
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberExited, MemberJoined}
import akka.cluster.{Cluster, Member}
import utils.messages.{ExecutorAddress, GetExecutorAddress}

import scala.util.Random

class ExecutorDirectoryServiceActor extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  var directory : Map[Address, Object] = Map(self.path.address -> null)

  override def receive: Receive = {
    case MemberJoined(member) => {
      directory += (member.address -> null)
    }
    case MemberExited(member) => {
      directory -= member.address
    }
    case GetExecutorAddress => getMember()
  }

  def getMember() = {
    /* until a health status is available we simply use a random approach */
    log.debug("received GetExecutorAddress - returning ExecutorAddress")
    sender() ! ExecutorAddress(new Random().shuffle(directory).head._1)
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
