import akka.actor.{ActorRef, ActorSystem, Props}
import worker.{TaskActor, WorkerActor}
import worker.messages.{AddTask, GetTask, Task}

import scala.io.StdIn

/**
  * Created by mischcon on 3/20/17.
  */
object main extends App{
  val system : ActorSystem = ActorSystem("the-cluster")
  println("hello from master!")

  val workerActor : ActorRef = system.actorOf(Props[WorkerActor], "distributor")

  val task = Task(null, false)

  workerActor ! AddTask(List("nodes"), task)
  workerActor ! AddTask(List("nodes"), task)
  workerActor ! AddTask(List("nodes", "rooms"), task)

  val taskActor : ActorRef = system.actorOf(Props[TaskActor], "task_actor")
  val taskActor2 : ActorRef = system.actorOf(Props[TaskActor], "task_actor2")
  val taskActor3 : ActorRef = system.actorOf(Props[TaskActor], "task_actor3")

  StdIn.readLine()
  system.terminate()
}
