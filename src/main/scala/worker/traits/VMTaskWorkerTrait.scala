package worker.traits

import akka.actor.{ActorRef, Terminated}
import worker.messages.Task

/**
  * Created by mischcon on 11.05.17.
  */
trait VMTaskWorkerTrait {

  /**
    * Response of a {@link worker.messages#GetTask GetTask} message.
    * A task is offered via a {@link worker.messages#SendTask SendTask} message.
    *
    * In case the offer gets accepted: Respond with a {@link worker.messages#AcquireExecutor AcquireExecutor} message
    * In case the offer gets rejected: Respond with a {@link akka.actor#Failure Failure} that contains an exception (e.g. new Exception("no more tasks!"))
    *
    * See wiki for more details about the workflow.
    * @param task The actual Task
    */
  def handlerSendTask(task : Task)

  /**
    * Response of a {@link worker.messages#GetTask GetTask} message.
    * This means that there are currently no more tasks available.
    *
    * See wiki for more details about the workflow.
    */
  def handlerNoMoreTasks()

  /**
    * Response of a {@link worker.messages#AcquireExecutor AcquireExecutor} message.
    * After a task offer gets accepted a new executor is being created. The ref of this
    * executor is passed to the target through this message.
    *
    * See wiki for more details about the workflow.
    * @param executor ActorRef of the executor
    */
  def handlerExecutor(executor : ActorRef)

  /**
    * Response of a {@link worker.messages#AcquireExecutor AcquireExecutor} message.
    * After a task offer gets accepted a new executor should be created - in case something goes wrong /
    * no executor could be created the target is informed about that through this message.
    *
    * See wiki for more details about the workflow.
    */
  def handlerCannotGetExecutor()

  /**
    * Since the target supervises the executor it has to react to the executors termination.
    * This can happen in case of a finished task execution or in case of an error (e.g. connection to
    * executor lost).
    *
    * See wiki for more details about the workflow.
    * @param terminated The actual Termianted message
    */
  def handlerTerminated(terminated : ActorRef)

  /**
    * If a task fails we need to check whether it has failed because of a failed VM (in this case the task
    * needs to be re-run) or because of something else (in this case the task does NOT needs to be re-run).
    * This check is done through this function.
    *
    * Respond with 'true' in case everything is ok.
    * Respond with anything else (or do not send any response within 3 seconds) in case something is wrong.
    *
    * See wiki for more details about the workflow.
    */
  def handlerStillAlive()
}
