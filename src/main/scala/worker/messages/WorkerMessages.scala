package worker.messages

import akka.actor.ActorRef

import java.lang.reflect.Method

/**
  * Created by mischcon on 21.03.17.
  */
trait WorkerMessagesTrait

case class AddTask(group : List[String], task : Task) extends WorkerMessagesTrait
case class Task(method : Method, singleInstance: Boolean) extends WorkerMessagesTrait

trait WorkerMessagesRecoveryTrait

case class AddTaskRecovery(group : List[String], task : Task) extends WorkerMessagesRecoveryTrait
case class TaskRecovery(method : Method, singleInstance : Boolean) extends WorkerMessagesRecoveryTrait