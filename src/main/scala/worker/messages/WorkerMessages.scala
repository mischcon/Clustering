package worker.messages

import akka.actor.ActorRef

import java.lang.reflect.Method

/**
  * Created by mischcon on 21.03.17.
  */
trait WorkerMessagesTrait

case class AddTask(group : List[String], task : Task) extends WorkerMessagesTrait
case class GetTask() extends WorkerMessagesTrait

case class SendTask(task : Task)

case class Task(method : Method, singleInstance: Boolean) extends WorkerMessagesTrait

trait WorkerMessagesRecoveryEvent

case class AddTaskRecovery(group : List[String], task : Task) extends WorkerMessagesRecoveryEvent
case class TaskRecovery(method : Method, singleInstance : Boolean) extends WorkerMessagesRecoveryEvent