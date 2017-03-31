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
  def uuid: String = java.util.UUID.randomUUID.toString

  implicit val timeout = Timeout(5 seconds)

  val sys = ActorSystem("actor-system")
  val db = sys.actorOf(Props[DBActor], name="db-actor")

  val method = uuid
  db ! CreateTask(method)

  val methods = List(uuid, uuid, uuid)
  db ! CreateTasks(methods)

  val future1 = db ? GetTask(method)
  val result1 = Await.result(future1, timeout.duration).asInstanceOf[Option[RequestedTask]]
  result1 match {
    case Some(task) =>
      println(s"[Future]: ${task.method} - ${task.task_status} - ${task.end_state} - ${task.task_result}")
    case None =>
      println("[Future]: no result")
  }

  val future2 = db ? GetTasks(methods)
  val result2 = Await.result(future2, timeout.duration).asInstanceOf[Option[List[RequestedTask]]]
  result2 match {
    case Some(tasks) =>
      for (task <- tasks)
        println(s"[Future]: ${task.method} - ${task.task_status} - ${task.end_state} - ${task.task_result}")
    case None =>
      println("[Future]: no result")
  }

  val future3 = db ? GetTasksWithStatus(TaskStatus.NOT_STARTED)
  val result3 = Await.result(future3, timeout.duration).asInstanceOf[Option[List[RequestedTask]]]
  result3 match {
    case Some(tasks) =>
      for (task <- tasks)
        println(s"[Future]: ${task.method} - ${task.task_status} - ${task.end_state} - ${task.task_result}")
    case None =>
      println("[Future]: no result")
  }

  db ! UpdateTask(method, TaskStatus.DONE, EndState.SUCCESS, "HTTP Response 200")
  db ! UpdateTasks(methods, TaskStatus.DONE, EndState.SUCCESS, "HTTP Response 201")
  db ! UpdateTaskStatus(method, TaskStatus.RUNNING)
  db ! UpdateTasksStatus(methods, TaskStatus.RUNNING)

  val future4 = db ? GetTasks(method :: methods)
  val result4 = Await.result(future4, timeout.duration).asInstanceOf[Option[List[RequestedTask]]]
  result4 match {
    case Some(tasks) =>
      for (task <- tasks)
        println(s"[Future]: ${task.method} - ${task.task_status} - ${task.end_state} - ${task.task_result}")
    case None =>
      println("[Future]: no result")
  }

  val future5 = db ? CountTaskStatus
  val result5 = Await.result(future5, timeout.duration).asInstanceOf[CountedTaskStatus]
//  println("[Future]: " + result5.result)

  val future6 = db ? CountEndState
  val result6 = Await.result(future6, timeout.duration).asInstanceOf[CountedEndState]
//  println("[Future]: " + result6.result)

  db ! DeleteTask(method)
  db ! DeleteTasks(methods)

  db ! "TEST"
}
