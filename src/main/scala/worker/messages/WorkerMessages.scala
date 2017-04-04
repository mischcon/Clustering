package worker.messages

import akka.actor.ActorRef

trait WorkerMessagesTrait

/* TASK DEPENDENCY TREE CREATION AND RETRIEVAL / DELETION */
case class AddTask(group : List[String], task : Task) extends WorkerMessagesTrait
case class GetTask() extends WorkerMessagesTrait

/* TASK SENDING AND RECEIVING */
case class SendTask(task : Task)
case class Task(raw_cls : Array[Byte], classname : String, method : String, singleInstance: Boolean) extends WorkerMessagesTrait
case object NoMoreTasks extends WorkerMessagesTrait

/* EXECUTORS */
case class AcquireExecutor(vmInfo : Object, vmActorRef : ActorRef) extends WorkerMessagesTrait
case class Executor(ref : ActorRef) extends WorkerMessagesTrait
case class ExecuteTask(task : Task, vmInfo : Object) extends WorkerMessagesTrait
case object CannotGetExecutor extends WorkerMessagesTrait

/* UTILITIES */
case class PersistAndSuicide(reason : String) extends WorkerMessagesTrait




