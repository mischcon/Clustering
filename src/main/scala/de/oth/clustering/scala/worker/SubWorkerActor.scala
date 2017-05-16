package de.oth.clustering.scala.worker

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorRef, ActorSelection, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.util.Timeout
import de.oth.clustering.scala.exceptions.{TestFailException, TestSuccessException}
import de.oth.clustering.scala.utils.db.{EndState, TaskStatus, UpdateTask}
import de.oth.clustering.scala.worker.messages._
import de.oth.clustering.scala.worker.traits.WorkerTrait

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

/**
  * Those actors have two different purposes:
  *   1) They act as an identifier - a SingleInstanceActor will always only be responsible for tasks that
  *      should be run on a single instance, while a GroupActor will always only be responsible for tasks
  *      that can be safely executed in parallel.
  *   2) They are the core of the whole task-dependency-management (see the wiki for further information about how the task dependency works)
  *
  * Each SingleInstanceActor / GroupActor contains two lists:
  *   1) A list that contains all children of type {@link de.oth.de.oth.clustering.java.clustering.scala.worker.TaskActor TaskActor} (all tasks that are part of the current level in the dependency tree)
  *   2) A list that contains all children of type SingleInstanceActor / GroupActor (the next level in the dependency tree)
  *
  * If a SingleInstanceActor / GroupActor receives a {@link de.oth.de.oth.clustering.java.clustering.scala.worker.messages#GetTask GetTask} request it will forward it to all its @link de.oth.de.oth.clustering.java.clustering.scala.worker.TaskActor TaskActors} if
  * there are any - if not, than this means that the current level in the dependency tree is done. In this case
  * the request is forwarded to all its (SingleInstanceActor / GroupActor) children.
  * If a SingleInstanceActor / GroupActor has no more children (neither TaskActor nor SingleInstanceActor / GroupActor)
  * than this means that all tasks have been processed - in this case the actor kills itsself.
  *
  * @param group Member array of the task dependency tree
  * @param tablename Name of the corresponding database table
  */
abstract class SubWorkerActor(var group : List[String], tablename : String) extends WorkerTrait{

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

  /**
    * Whether or not a task was successful an exception ({@link de.oth.de.oth.clustering.java.clustering.scala.Exceptions#TestSuccessException TestSuccessException} or
    * {@link de.oth.de.oth.clustering.java.clustering.scala.Exceptions#TestFailException TestFailException})
    * will be thrown by the {@link de.oth.de.oth.clustering.java.clustering.scala.worker#TaskExecutorActor TaskExecutorActor} and escalated to its parent GroupActor or SingleInstanceActor.
    *
    * In case of a success, see {@link de.oth.de.oth.clustering.java.clustering.scala.worker.SubWorkerActor#handleSuccess handleSuccess()}.
    * In case of a failure, see {@link de.oth.de.oth.clustering.java.clustering.scala.worker.SubWorkerActor#handleFailure handleFailure()}.
    *
    * In both cases the TaskActor should be stopped.
    * @return
    */
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

  /**
    * Handles the success of a task.
    * Writes the result with the updated status of the task to the database.
    * Removes the ActorRef of the {@link de.oth.de.oth.clustering.java.clustering.scala.worker#TaskActor TaskActor} from the taskActors list.
    * @param task Task - needed to identify classname and methodname
    * @param result Result of the task execution
    * @param source ActorRef of TaskActor
    */
  def handleSuccess(task : Task, result : Object, source : ActorRef): Unit ={
    /* DB Actor + write */
    log.debug(s"writing ${result} result to db")
    dbActor ! UpdateTask(s"${task.classname}.${task.method}", TaskStatus.DONE, EndState.SUCCESS,
      if (result != null && result.toString == "IGNORE") result.toString else "", tablename)

    taskActors = taskActors.filter(x => x != source)
  }

  /**
    * Handles the failure of a task.
    * Writes the result with the updated status of the task to the database.
    * Removes the ActorRef of the {@link de.oth.de.oth.clustering.java.clustering.scala.worker#TaskActor TaskActor} from the taskActors list.
    *
    * Since a failed task in the dependency tree should stop the execution of any task on the
    * same level / on lower levels a {@link de.oth.de.oth.clustering.java.clustering.scala.worker.messages#PersistAndSuicide PersistAndSuicide} message is sent to all children.
    * @param task Task - needed to identify classname and methodname
    * @param result Result of the task execution
    * @param source ActorRef of {@link de.oth.de.oth.clustering.java.clustering.scala.worker#TaskActor TaskActor}
    */
  def handleFailure(task : Task, result : Throwable, source : ActorRef): Unit = {
    /* DB Actor + write */
    log.debug(s"writing FAILURE result to db")
    var res = result
    var toWrite = null.asInstanceOf[String]
    if(res.getCause != null)
      res = res.getCause
    if(res != null)
      toWrite = res.toString
    dbActor ! UpdateTask(s"${task.classname}.${task.method}", TaskStatus.DONE, EndState.FAILURE, toWrite, tablename)

    taskActors = taskActors.filter(x => x != source)

    // advice all taskActor to write their results to DB and commit suicide
    context.children.foreach(x => x ! PersistAndSuicide(s"${task.classname}.${task.method}"))
  }

  /**
    * Checks if there are still children present.
    * If not, than the actor kills itsself.
    */
  def check_suicide(): Unit ={
    if(context.children.isEmpty) {
      log.debug("no more tasks available and no more children present - performing suicide for the greater good")
      context.stop(self)
    } else {
      log.debug(s"there are still children that depend on ${self.path.toString} - I will stay in this world (taskActors: ${taskActors.size} | children: ${context.children.size})")
    }
  }

  /**
    * Creates SingleInstanceActors / GroupActors (if there are no suitable) and forwards incoming
    * {@link de.oth.de.oth.clustering.java.clustering.scala.worker.messages#AddTask} messages to them.
    * Adds the ActorRef of the created actor to the taskActors list.
    * @param msg
    */
  def addTask(msg : AddTask) = {
    // create new de.oth.de.oth.clustering.java.clustering.scala.worker
    if(msg.group.length > group.length){
      val name = msg.group.take(group.length + 1)

      context.child(name.mkString(".")) match {
        case Some(child) => child ! msg
        case None => {
          msg.task.singleInstance match {
            case false => {
              val ref = context.actorOf(Props(classOf[GroupActor], name, tablename), name.mkString("."))
              ref ! msg
              context.watch(ref)
            }
            case true => {
              val ref = context.actorOf(Props(classOf[SingleInstanceActor], name, tablename), name.mkString("."))
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
      val ref = context.actorOf(Props(classOf[TaskActor], msg.task, tablename), s"TASK-${msg.task.method}-${new Random().nextLong()}")
      context.watch(ref)
      taskActors = ref :: taskActors
    }
  }

  /**
    * Forwards {@link de.oth.de.oth.clustering.java.clustering.scala.worker.messages#GetTask} messages to all its {@link de.oth.de.oth.clustering.java.clustering.scala.worker#TaskActor TaskActor} children.
    * If there are no more {@link de.oth.de.oth.clustering.java.clustering.scala.worker#TaskActor TaskActor} children left it will forward the message to all its
    * SingleInstanceActor / GroupActor children.
    * @param msg
    */
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
    check_suicide()
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

class GroupActor(group : List[String], tablename : String) extends SubWorkerActor(group, tablename)
class SingleInstanceActor(group : List[String], tablename : String) extends SubWorkerActor(group, tablename)
