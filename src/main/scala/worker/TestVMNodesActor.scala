package worker

import akka.actor.{Actor, Props}
import akka.pattern._
import akka.actor.Actor.Receive
import worker.messages._

import scala.util.{Failure, Success}

/**
  * Created by mischcon on 26.03.2017.
  */
class TestVMNodesActor extends Actor{

  var haveSpaceForTasks = true

  override def receive: Receive = {
    case "get" => context.system.actorSelection("/user/distributor") ! GetTask
    case t : SendTask if haveSpaceForTasks => {
      haveSpaceForTasks = false
      val executor = context.actorOf(Props(classOf[TaskExecutorActor], t.task, null), t.task.method.getName)
      sender() ! executor
    }
    case t : Result => {
      // write result to database or something
      println("result present - writing it to db")
    }
  }
}
