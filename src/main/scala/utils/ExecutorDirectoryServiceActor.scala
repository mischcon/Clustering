package utils

import akka.actor.{Actor, ActorLogging, Address}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
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
    case UnreachableMember(member) => {
      log.debug(s"MEMBER UNREACHABLE! Goodbye my friend at ${member.address.toString}")
      directory -= member.address
      log.debug(s"DOWNING my fellow friend at ${member.address.toString}")
      cluster.down(member.address)
    }
    case GetExecutorAddress => getMember()
    case a => log.debug(s"RECEIVED SOMETHING UNEXPECTED: $a")
  }

  def getMember() = {
    /* until a health status is available we simply use a random approach */
    log.debug("received GetExecutorAddress - returning ExecutorAddress")
    var addr : Address = new Random().shuffle(directory).head._1

    // TODO: REMOVE only present for testing purpose
    if(directory.exists(x => x._1 != self.path.address))
      addr = new Random().shuffle(directory).filter(x => x._1 != self.path.address).head._1
    else
      addr = self.path.address

    sender() ! ExecutorAddress(addr)
  }

  override def preStart(): Unit = {
    super.preStart()
    log.debug("Hello from ExecutorDirectoryService")
    log.debug("Now reacting on MemberJoined and MemberExited Cluster Events")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberJoined], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug("Goodbye from ExecutorDirectoryService")
    cluster.unsubscribe(self)
  }
}
