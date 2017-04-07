package worker

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import worker.messages.{AddTask, GetTask, NoMoreTasks}

class InstanceActor extends Actor with ActorLogging{

  // InstanceID + Ref of child + Version
  var instances : List[(String, ActorRef, String)] = List.empty

  override def preStart(): Unit = {
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"Goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case p : AddTask => handleAddTask(p)
    case p : GetTask => handleGetTask(p)
    case t : Terminated => instances = instances.filter(a => a._2 != t.actor)
  }

  def handleAddTask(msg : AddTask): Unit ={
    log.debug("add task")
    context.child(msg.instanceId) match {
      case Some(child) => child ! msg
      case None => {
        val ref = context.actorOf(Props(classOf[DistributorActor]), msg.instanceId)
        instances = (msg.instanceId, ref, msg.version) :: instances
        ref ! msg

        context.watch(ref)
      }
    }
  }

  def handleGetTask(msg : GetTask): Unit = {
    if(!instances.exists(a => a._3 == msg.version)){
      log.debug("No more tasks available")
      sender() ! NoMoreTasks
    } else {
      instances.filter(a => a._3 == msg.version).sortBy(a => a._1).head._2 forward msg
    }
  }
}
