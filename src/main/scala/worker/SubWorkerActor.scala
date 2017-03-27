package worker

import Exceptions.{TestFailException, TestSuccessException}
import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{ActorRef, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy, Terminated}
import akka.util.Timeout
import worker.messages._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

abstract class SubWorkerActor(var group : List[String]) extends WorkerTrait{

  implicit val timeout = Timeout(2 seconds)
  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  var taskActors : List[ActorRef] = Nil

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
    case Terminated => taskActors = taskActors.filter(x => x != sender())
    case x => log.debug(s"${self.path.name} received something unexpected: $x")
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){
    case t : TestFailException => {
      /* Test as failed - this means that the entire node should be stopped */
      handleFailure(t.task, t.result)
      Stop
    }
    case t : TestSuccessException => {
      handleSuccess(t.task, t.result)
      Stop
    }
  }

  def handleSuccess(task : Task, result : Object): Unit ={
    /* DB Actor + write */
    log.debug("writing SUCCESS result to db")
  }

  def handleFailure(task : Task, result : Object): Unit = {
    /* DB Actor + write */
    log.debug("writing FAILURE result to db")

    /* stop all children */
    context.children.foreach(x => x ! PoisonPill)
    self ! PoisonPill
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
      taskActors = context.actorOf(Props(classOf[TaskActor], msg.task), s"TASK-${msg.task.method.getName}-${new Random().nextLong()}") :: taskActors
    }
  }

  def getTask(msg : GetTask) = {
    // check if there is any free task actor left
    if(taskActors.nonEmpty) {
      log.debug(s"received GetTask - forwarding it to taskActors (have ${taskActors.size})")
      taskActors.foreach(x => x forward msg)
    }
    // if not, pass the message to your children
    else {
      log.debug(s"received GetTask - forwarding it to children (have ${context.children.size})")
      context.children.foreach(x => x forward msg)
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
