package worker

import java.lang.reflect.Method

import akka.actor.Actor
import akka.actor.Actor.Receive
import worker.messages.Task

/**
  * Created by mischcon on 21.03.17.
  */
class TaskActor extends Actor{
  override def receive: Receive = {
    case t : Task => {
      /*
      * TODO
      *
      * > get targetVM
      * */
      println("attempting to run method " + t.method.getName() + s" as SingleInstance? (${t.singleInstance})")
    }
  }
}
