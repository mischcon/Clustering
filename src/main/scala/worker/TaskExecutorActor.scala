package worker
import akka.actor.Actor.Receive
import worker.messages.Task

/**
  * Created by mischcon on 26.03.2017.
  */
class TaskExecutorActor(task : Task, vmInfo : Object) extends WorkerTrait{
  override def receive: Receive = {
    case _ => println()
  }
}
