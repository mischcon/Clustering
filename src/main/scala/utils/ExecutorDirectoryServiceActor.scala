package utils

import akka.actor.{Actor, ActorLogging, Address}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberExited, MemberJoined}
import utils.messages.{ExecutorAddress, GetExecutorAddress}

import scala.util.Random

class ExecutorDirectoryServiceActor extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  var directory : Map[Address, Object] = Map(self.path.address -> null)

  override def receive: Receive = {
    case MemberJoined(member) => {
      log.debug(s"MEMBER JOINED! Hello my friend at ${member.address.toString}")
      directory += (member.address -> null)
    }
    case MemberExited(member) => {
      log.debug(s"MEMBER EXITED! Goodbye my friend at ${member.address.toString}")
      directory -= member.address
    }
    case GetExecutorAddress => getMember()
    case a => log.debug(s"RECEIVED SOMETHING UNEXPECTED: $a")
  }

  def getMember() = {
    /* until a health status is available we simply use a random approach */
    log.debug("received GetExecutorAddress - returning ExecutorAddress")
    var addr : Address = new Random().shuffle(directory).head._1

    // TODO: REMOVE only present for testing purpose
    addr = new Random().shuffle(directory.filter( x => x._1 != self.path.address).map(x => x)).head._1

    sender() ! ExecutorAddress(addr)
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
