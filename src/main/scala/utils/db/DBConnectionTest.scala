package utils.db

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * = Test application for [[utils.db.DBActor]] =
  * will be removed later
  */
object DBConnectionTest extends App {
  def uuid: String = {
    java.util.UUID.randomUUID.toString
  }

  implicit val timeout = Timeout(5 seconds)

  val sys = ActorSystem("actor-system")
  val db = sys.actorOf(Props[DBActor], name="db-actor")

  val method = uuid
  db ! CreateTask(method)

  val future1 = db ? GetTask(method)
  val result1 = Await.result(future1, timeout.duration).asInstanceOf[Option[RequestedTask]]
  result1 match {
    case Some(task) =>
      println(s"${task.method} - ${task.status} - ${task.result}")
    case None =>
      println("no result")
  }

  db ! UpdateTaskStatus(method, TaskStatus.RUNNING)
  db ! DeleteTask(method)

  val methods = List(uuid, uuid, uuid)
  db ! CreateTasks(methods)

  val future2 = db ? GetTasksWithStatus(TaskStatus.RUNNING)
  val result2 = Await.result(future2, timeout.duration).asInstanceOf[Option[List[RequestedTask]]]
  result2 match {
    case Some(tasks) =>
      for (task <- tasks) {
        println(s"${task.method} - ${task.status} - ${task.result}")
      }
    case None =>
      println("no result")
  }

  db ! UpdateTasksStatus(methods, TaskStatus.DONE)

  val future3 = db ? GetTasks(methods)
  val result3 = Await.result(future3, timeout.duration).asInstanceOf[Option[List[RequestedTask]]]
  result3 match {
    case Some(tasks) =>
      for (task <- tasks) {
        println(s"${task.method} - ${task.status} - ${task.result}")
      }
    case None =>
      println("no result")
  }

  db ! DeleteTasks(methods)
}
