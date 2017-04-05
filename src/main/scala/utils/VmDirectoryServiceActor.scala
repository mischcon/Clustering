package utils

import akka.actor.{Actor, ActorLogging, Address}
import akka.actor.Actor.Receive
import akka.cluster.Cluster

class VmDirectoryServiceActor extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  // Address +
  var directory : Map[Address, Object] = Map(self.path.address -> null)

  override def receive: Receive = ???
}
