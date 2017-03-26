import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import worker.{TaskActor, DistributorActor}
import worker.messages.{AddTask, GetTask, Task}

import scala.io.StdIn

/**
  * Created by mischcon on 3/20/17.
  */
object main extends App{

  val config = ConfigFactory.load()

  val system : ActorSystem = ActorSystem("the-cluster", config.getConfig("master").withFallback(config))
  println("hello from master!")

  val workerActor : ActorRef = system.actorOf(Props[DistributorActor], "distributor")

  val task = Task(null, false)

  workerActor ! AddTask(List("nodes"), task)
  workerActor ! AddTask(List("nodes"), task)
  workerActor ! AddTask(List("nodes", "rooms"), task)

  val taskActor : ActorRef = system.actorOf(Props(classOf[TaskActor], null), "task_actor")
  val taskActor2 : ActorRef = system.actorOf(Props(classOf[TaskActor], null), "task_actor2")
  val taskActor3 : ActorRef = system.actorOf(Props(classOf[TaskActor], null), "task_actor3")

  StdIn.readLine()
  system.terminate()
}
