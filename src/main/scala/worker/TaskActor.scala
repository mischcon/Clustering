package worker

import worker.messages.{Result, SendTask, Task}

/**
  * Created by mischcon on 21.03.17.
  */
class TaskActor(task : Task) extends WorkerTrait{

  var isTaken : Boolean = false

  override def preStart(): Unit = {
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"Goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case t : SendTask if ! isTaken => {
      isTaken = true
      log.debug("received a task! - returning own actor ref for supervision purpose")
      sender ! self

      val result : Object = t.task.method
      context.parent ! Result(result)
      context.stop(self)
    }
  }
}
