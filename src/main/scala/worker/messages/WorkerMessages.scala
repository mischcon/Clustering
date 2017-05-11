package worker.messages

import akka.actor.ActorRef
import utils.DeployInfoInterface
import vm.vagrant.configuration.VagrantEnvironmentConfig

trait WorkerMessagesTrait

/* TASK DEPENDENCY TREE CREATION AND RETRIEVAL / DELETION */
/**
  * Used for adding tasks
  * @param instanceId identifies the task run
  * @param group members of the dependency tree
  * @param task the actual task
  * @param version VM deploy info
  */
case class AddTask(instanceId : String, group : List[String], task : Task, version : DeployInfoInterface = null) extends WorkerMessagesTrait

/**
  * Used for getting a task
  * Request for a {@link worker.messages#SendTask SendTask} response
  * @param version VM deploy info - the response (task) should have the same VM deploy info
  */
case class GetTask(version : String) extends WorkerMessagesTrait

/* TASK SENDING AND RECEIVING */

/**
  * Used for sending a task
  * Reply of a {@link worker.messages#GetTask GetTask} message
  * @param task the actual task
  */
case class SendTask(task : Task)

/**
  * Container message for a task
  * @param raw_cls ByteArray representing the class
  * @param classname the name of the class
  * @param method the name of the method
  * @param singleInstance indicates whether this task can be executed in parallel with other tasks or if should be
  *                       run on a single instance
  */
case class Task(raw_cls : Array[Byte], classname : String, method : String, singleInstance: Boolean) extends WorkerMessagesTrait

/**
  * Indicates that there are no more tasks (for a given version string) available
  */
case object NoMoreTasks extends WorkerMessagesTrait

/* EXECUTORS */
/**
  * Used for requesting an executor from the {@link utils.ExecutorDirectoryServiceActor ExecutorDirectoryServiceActor}
  * @param vmActorRef actor ref of the target {@link vm#VMProxyActor VMProxyActor}
  */
case class AcquireExecutor(vmActorRef : ActorRef) extends WorkerMessagesTrait

/**
  * Container class for the actor ref of a {@link worker.TaskExecutorActor TaskExecutorActor}
  * @param ref
  */
case class Executor(ref : ActorRef) extends WorkerMessagesTrait

/**
  * Container class and command message for the execution of a task
  * @param task the actual task
  * @param targetVM actor ref of the target {@link vm#VMProxyActor VMProxyActor}
  */
case class ExecuteTask(task: Task, targetVM: ActorRef) extends WorkerMessagesTrait

/**
  * Indicates that no executor could have been created
  */
case object CannotGetExecutor extends WorkerMessagesTrait

/* UTILITIES */
/**
  * Command message sent to {@link worker.TaskActor TaskActor} indicating that a dependency in the
  * dependency tree has failed. The target {@link worker.TaskActor TaskActors} will update the status
  * of their task and will stop themselves
  * @param reason reason (usually a reference to the failed task)
  */
case class PersistAndSuicide(reason : String) extends WorkerMessagesTrait

/**
  * Asks a {@link vm#VMProxyActor VMProxyActor} if it is still alive
  */
case object StillAlive extends WorkerMessagesTrait

/* DEPLOYMENT */
/**
  * Request message that asks for deploy information.
  * Results in responding with a {@link worker.messages#DeployInfo DeployInfo} or
  * {@link worker.messages#NoDeployInfo NoDeployInfo} message.
  */
case object GetDeployInfo extends WorkerMessagesTrait

/**
  * Container message for deploy info
  * Reply of a {@link worker.messages#GetDeployInfo GetDeployInfo} message.
  * @param vagrantEnvironmentConfig the actual deploy info
  */
case class DeployInfo[T >: DeployInfoInterface](vagrantEnvironmentConfig : T) extends WorkerMessagesTrait

/**
  * Indicates that there is no deploy info available currently
  * Reply of a {@link worker.messages#GetDeployInfo GetDeployInfo} message.
  */
case object NoDeployInfo extends WorkerMessagesTrait




