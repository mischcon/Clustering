package worker

import java.lang.reflect.Method

import akka.pattern._
import akka.actor.Actor
import akka.actor.Actor.Receive
import worker.messages.{GetTask, SendTask, Task}

/**
  * Created by mischcon on 21.03.17.
  */
class TaskActor extends Actor{

  println("task actor created!")
  // request new Task
  context.system.actorSelection("user/distributor") ! GetTask()

  override def receive: Receive = {
    case t : SendTask => {
      println("received a task! - returning own actor ref for supervision purpose")
      sender ! self
    }
  }
}
