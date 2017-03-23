package worker

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import worker.messages._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.concurrent.duration._

abstract class SubWorkerActor(var group : List[String]) extends WorkerTrait{

  implicit val timeout = Timeout(2 seconds)
  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  var tasks : List[Task] = Nil

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
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
    // or add the task to the list in this worker
    else {
      println(s"task was added to ${self.path.name}")
      tasks = msg.task :: tasks
    }
  }

  def getTask(msg : GetTask) = {
    // check if there are still tasks in this node
    if(!tasks.isEmpty){
      // take one task
      val task_to_run = tasks.last
      tasks = tasks.init
      println(s"task was taken from ${self.path.name} awaiting actor ref....")
      /* pass the task to the node actor and request the actorRef of the execution actor
      *
      * This is neccessary because now we can supervise the executing actor
      * */
      val actorRef_of_executing_actor = sender() ? SendTask(task_to_run)
      actorRef_of_executing_actor.onComplete {
        case Success(ref : ActorRef) => {
          println(s"${self.path.name} received an actor ref! now monitoring the actor...")
          context.watch(ref)
        }
        case Failure(ex : Throwable) => {
          // put task back in list
          tasks = task_to_run :: tasks
          println(s"NodeActor did not return the ActorRef of the executing actor: ${ex.getMessage}")
        }
      }
    } else {
      // if not, forward the message to one of your children
      println(s"no tasks left in ${self.path.name} - forwarding to child")
      context.children.head forward msg
    }
  }
}

class GroupActor(group : List[String]) extends SubWorkerActor(group) {
  println(self.path.name + " was created!")
}
class SingleInstanceActor(group : List[String]) extends SubWorkerActor(group) {
  print(self.path.name + " was created!")
}
