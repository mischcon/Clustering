package worker

import Exceptions.{TestFailException, TestSuccessException}
import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorRef, ActorSelection, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.util.Timeout
import utils.db.{EndState, TaskStatus, UpdateTask}
import worker.messages._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

abstract class SubWorkerActor(var group : List[String]) extends WorkerTrait{

  implicit val timeout = Timeout(1 seconds)
  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  val dbActor : ActorSelection = context.system.actorSelection("/user/db")
  var taskActors : List[ActorRef] = Nil

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
    case t : Terminated => check_suicide()
    case x : PersistAndSuicide => {
      taskActors = List.empty
      context.children.foreach(a => a ! x)
    }
    case x => log.warning(s"${self.path.name} received something unexpected: $x")
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){
    case t : TestFailException => {
      /* Test as failed - this means that the entire node should be stopped */
      handleFailure(t.task, t.result, sender())
      Stop
    }
    case t : TestSuccessException => {
      handleSuccess(t.task, t.result, sender())
      Stop
    }
  }

  def handleSuccess(task : Task, result : Object, source : ActorRef): Unit ={
    /* DB Actor + write */
    log.debug("writing SUCCESS result to db")
    // TODO: tableName dbActor ! UpdateTask(s"${task.classname}.${task.method}", TaskStatus.DONE, EndState.SUCCESS, null)

    taskActors = taskActors.filter(x => x != source)
  }

  def handleFailure(task : Task, result : Throwable, source : ActorRef): Unit = {
    /* DB Actor + write */
    log.debug(s"writing FAILURE result to db")
    // TODO: tableName dbActor ! UpdateTask(s"${task.classname}.${task.method}", TaskStatus.DONE, EndState.FAILURE, result.getCause.toString)

    taskActors = taskActors.filter(x => x != source)

    // advice all taskActor to write their results to DB and commit suicide
    context.children.foreach(x => x ! PersistAndSuicide(s"${task.classname}.${task.method}"))
  }

  def check_suicide(): Unit ={
    if(taskActors.isEmpty && context.children.isEmpty) {
      log.debug("no more tasks available and no more children present - performing suicide for the greater good")
      context.stop(self)
    } else {
      log.debug(s"there are still children that depend on ${self.path.toString} - I will stay in this world (taskActors: ${taskActors.size} | children: ${context.children.size})")
    }
  }

  def addTask(msg : AddTask) = {
    // create new worker
    if(msg.group.length > group.length){
      val name = msg.group.take(group.length + 1)

      context.child(name.mkString(".")) match {
        case Some(child) => child ! msg
        case None => {
          msg.task.singleInstance match {
            case false => {
              val ref = context.actorOf(Props(classOf[GroupActor], name), name.mkString("."))
              ref ! msg
              context.watch(ref)
            }
            case true => {
              val ref = context.actorOf(Props(classOf[SingleInstanceActor], name), name.mkString("."))
              ref ! msg
              context.watch(ref)
            }
          }
        }
      }
    }
    // or create new TaskActor
    else {
      log.debug(s"task was added to ${self.path.name}")
      val ref = context.actorOf(Props(classOf[TaskActor], msg.task), s"TASK-${msg.task.method}-${new Random().nextLong()}")
      context.watch(ref)
      taskActors = ref :: taskActors
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
    super.preStart()
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug(s"Goodbye from ${self.path.name}")
  }
}

class GroupActor(group : List[String]) extends SubWorkerActor(group)
class SingleInstanceActor(group : List[String]) extends SubWorkerActor(group)
