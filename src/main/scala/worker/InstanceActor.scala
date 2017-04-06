package worker

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import worker.messages.{AddTask, GetTask}

class InstanceActor extends Actor with ActorLogging{

  var instances : List[(String, ActorRef, String)] = List.empty

  override def receive: Receive = {
    case p : AddTask => handleAddTask(p)
    case p : GetTask => handleGetTask(p)
    case t : Terminated => instances = instances.filter(a => a._2 != t.actor)
  }

  def handleAddTask(msg : AddTask): Unit ={
    context.child(msg.instanceId) match {
      case Some(child) => child ! msg
      case None => {
        val ref = context.actorOf(Props[DistributorActor], msg.instanceId)
        instances = (msg.instanceId, ref, msg.version) :: instances
        ref ! msg

        context.watch(ref)
      }
    }
  }

  def handleGetTask(msg : GetTask): Unit = {
    // filter for version, then sort for instanceId
    instances.filter(a => a._3 == msg.version).sortBy(a => a._1).head._2 forward msg
  }
}
