package worker

import akka.actor.{ActorRef, Props}
import akka.util.Timeout
import worker.messages._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class SubWorkerActor(var group : List[String]) extends WorkerTrait{

  implicit val timeout = Timeout(2 seconds)
  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  var taskActors : List[ActorRef] = Nil

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
    case r : Result => {
      // inform somebody that the test was successful
      context.parent ! r
      taskActors = taskActors.filter(x => x != sender())
    }
    case x => log.debug(s"${self.path.name} received something unexpected: $x")
  }

  def addTask(msg : AddTask) = {
    // create new worker
    if(msg.group.length > group.length){
      val name = msg.group.take(group.length + 1)

      // check if we need to create a group actor or single instance actor
      if(msg.task.singleInstance)
        context.actorOf(Props(classOf[SingleInstanceActor], name), name.mkString(".")) ! msg
      else
        context.actorOf(Props(classOf[GroupActor], name), name.mkString(".")) ! msg
    }
    // or create new TaskActor
    else {
      log.debug(s"task was added to ${self.path.name}")
      taskActors = context.actorOf(Props(classOf[TaskActor], msg.task), s"TASK-${msg.task.method.getName}") :: taskActors
    }
  }

  def getTask(msg : GetTask) = {
    // check if there is any free task actor left
    if(taskActors.nonEmpty)
      taskActors.foreach(x => x forward msg)
    // if not, pass the message to your children
    else
      context.children.foreach(x => x forward msg)
  }

  override def preStart(): Unit = {
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"Goodbye from ${self.path.name}")
  }
}

class GroupActor(group : List[String]) extends SubWorkerActor(group)
class SingleInstanceActor(group : List[String]) extends SubWorkerActor(group)
