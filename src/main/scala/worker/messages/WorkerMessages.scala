package worker.messages

import akka.actor.ActorRef

/**
  * Created by mischcon on 21.03.17.
  */
trait WorkerMessagesTrait

case class AddTask(group : List[String], task : Task) extends WorkerMessagesTrait
case class GetTask() extends WorkerMessagesTrait
case class PersistAndSuicide(reason : String) extends WorkerMessagesTrait
case class StopAllAndDie(reason : String) extends WorkerMessagesTrait
case class TaskActorInfo(task : Task) extends WorkerMessagesTrait
case class AcquireExecutor(vmInfo : Object, vmActorRef : ActorRef) extends WorkerMessagesTrait
case class Executor(ref : ActorRef) extends WorkerMessagesTrait
case class ExecuteTask(task : Task, vmInfo : Object) extends WorkerMessagesTrait
case class SendTask(task : Task)
case class Task(raw_cls : Array[Byte], classname : String, method : String, singleInstance: Boolean) extends WorkerMessagesTrait