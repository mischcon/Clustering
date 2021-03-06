package de.oth.clustering.scala.utils

import akka.actor.{Actor, ActorLogging, Address}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import de.oth.clustering.scala.utils.messages.{ExecutorAddress, GetExecutorAddress}

import scala.util.Random

/**
  * Executor Directory Service
  *
  * Keeps track of all physical nodes of the cluster that have the 'executor' role.
  *
  * Reacts to 'MemberJoined' and 'UnreachableMember' cluster events.
  *
  * {@link de.oth.de.oth.clustering.java.clustering.scala.utils.messages#GetExecutorAddress GetExecutorAddress} result in returning the address of
  * a suitable physical node through a {@link de.oth.de.oth.clustering.java.clustering.scala.utils.messages#ExecutorAddress ExecutorAddress} message.
  */
class ExecutorDirectoryServiceActor extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  /** Executor directory - value of the map can be used for loadbalancing purposes **/
  var directory : Map[Address, Object] = Map(self.path.address -> null)

  override def receive: Receive = {
    case MemberJoined(member) => {
      log.info(s"MEMBER JOINED! Hello my friend at ${member.address.toString}")
      if(member.hasRole("executor")){
        log.info("Member has role EXECUTOR - adding it to the executor list")
        directory += (member.address -> null)
      }

    }
//    case UnreachableMember(member) => {
//      log.info(s"MEMBER UNREACHABLE! Goodbye my friend at ${member.address.toString}")
//      directory -= member.address
//      log.info(s"DOWNING my fellow friend at ${member.address.toString}")
//      cluster.down(member.address)
//    }
    case MemberExited(member) => {
      log.info(s"MEMBER EXITED! Goodbye my friend at ${member.address.toString}")
      directory -= member.address
    }
    case GetExecutorAddress => getMember()
    case a => log.warning(s"received unexpected message: $a")
  }

  /**
    * Return the address of a physical node that can be used to create a new
    * {@link de.oth.de.oth.clustering.java.clustering.scala.worker.TaskExecutorActor TaskExecutorActor} through a
    * {@link de.oth.de.oth.clustering.java.clustering.scala.utils.messages#ExecutorAddress ExecutorAddress} message
    */
  def getMember() = {
    /* until a health status is available we simply use a random approach */
    log.debug("received GetExecutorAddress - returning ExecutorAddress")
    var addr : Address = new Random().shuffle(directory).head._1
    sender() ! ExecutorAddress(addr)
  }

  override def preStart(): Unit = {
    super.preStart()
    log.debug("Hello from ExecutorDirectoryService")
    log.debug("Now reacting on MemberJoined and UnreachableMember Cluster Events")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberJoined], classOf[MemberExited])// classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug("Goodbye from ExecutorDirectoryService")
    cluster.unsubscribe(self)
  }
}
