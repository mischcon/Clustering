package worker

import akka.actor.Actor.Receive
import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.pattern._
import akka.util.Timeout
import worker.messages._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.concurrent.duration._

abstract class SubWorkerActor(var group : List[String]) extends WorkerTrait{

  implicit val timeout = Timeout(2 seconds)
  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global
  var tasks : List[(Task, Object, String, ActorRef)] = Nil

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
    case x => log.debug(s"${self.path.name} received something unexpected: $x")
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){
    case cause:Throwable =>
      log.debug(s"${self.path.name} received throwable from child - stopping child")
      Stop
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
      log.debug(s"task was added to ${self.path.name}")
      tasks = (msg.task, null, "not running", null) :: tasks
    }
  }

  def getTask(msg : GetTask) = {
    // check if there are still tasks in this node
    if(tasks.exists(p => p._3 == "not running")){
      // take one task
      val task_to_run = tasks.filter(p => p._3 == "not running").head
      tasks = tasks.filter(p => p != task_to_run)
      log.debug(s"task was taken from ${self.path.name} awaiting actor ref....")
      /* pass the task to the node actor and request the actorRef of the execution actor
      *
      * This is neccessary because now we can supervise the executing actor
      * */
      val actorRef_of_executing_actor = sender() ? SendTask(task_to_run._1)
      actorRef_of_executing_actor.onComplete {
        case Success(ref : ActorRef) => {
          log.debug(s"${self.path.name} received an actor ref! now monitoring the actor...")
          context.watch(ref)
          tasks = (task_to_run._1, null, "pending", ref) :: tasks
        }
        case Failure(ex : Throwable) => {
          // put task back in list
          tasks = (task_to_run._1, null, "not running", null) :: tasks
          log.debug(s"NodeActor did not return the ActorRef of the executing actor: ${ex.getMessage}")
        }
      }
    } else if(context.children.nonEmpty) {
      // if not, forward the message to one of your children if there are any
      log.debug(s"no tasks left in ${self.path.name} - forwarding to child")
      context.children.head forward msg
    } else {
      log.debug(s"neither tasks nor children in ${self.path.name} - escalating to parent")
      context.parent ! Escalate
    }
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
