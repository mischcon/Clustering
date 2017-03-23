package utils.db

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

object DBConnectionTest extends App {
  def uuid: String = {
    java.util.UUID.randomUUID.toString
  }

  implicit val timeout = Timeout(2 seconds)

  val sys = ActorSystem("actor-system")
  val db = sys.actorOf(Props[DBActor], name="db-actor")
  val method = uuid

  db ! CreateTask(method)

  val future = db ? ReadTask(method)
  val result = Await.result(future, timeout.duration).asInstanceOf[Option[RequestedTask]]
  result match {
    case Some(task) =>
      println(s"${task.method} - ${task.status} - ${task.result}")
    case None =>
  }

  db ! UpdateTask(method, TaskStatus.RUNNING)
  db ! DeleteTask(method)
}
