package worker

import java.lang.reflect.Method

import akka.pattern._
import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import utils.LoggingActor
import worker.messages.{GetTask, SendTask, Task}

/**
  * Created by mischcon on 21.03.17.
  */
class TaskActor(targetVM : ActorRef) extends WorkerTrait{

  override def preStart(): Unit = {
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"Goodbye from ${self.path.name}")
  }

  // request new Task
  context.system.actorSelection("user/distributor") ! GetTask()

  override def receive: Receive = {
    case t : SendTask => {
      log.debug("received a task! - returning own actor ref for supervision purpose")
      sender ! self

    }
  }
}
