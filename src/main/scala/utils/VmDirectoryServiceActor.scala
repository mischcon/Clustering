package utils

import akka.actor.{Actor, ActorLogging, Address}
import akka.actor.Actor.Receive
import akka.cluster.Cluster
import akka.remote.ContainerFormats.ActorRef

class VmDirectoryServiceActor extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  // ActorRef of VmActor + its current Version / Deployed SDS Version
  var directory : Map[ActorRef, Object] = Map.empty

  override def receive: Receive = ???
}
