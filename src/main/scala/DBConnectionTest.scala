import java.sql.{Connection, DriverManager, Statement}

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

trait DBMsg
case class AddTask(method : String) extends DBMsg

class DBActor extends Actor {

  def connect: Option[Connection] = {
    try {
      val config = ConfigFactory.load()
      Class.forName(config.getString("db.driver"))
      Some(DriverManager.getConnection(
        config.getString("db.url"),
        config.getString("db.username"),
        config.getString("db.password")))
    }
    catch {
      case e: Exception =>
        println(e.getMessage)
        None
    }
  }

  def addTask(method : String): Unit = {
    connect match {
      case Some(connection) =>
        val statement : Statement = connection.createStatement()
        val sql : String = s"INSERT INTO `tasks`(`method`) VALUES ('$method');"
        try {
          // auto commit is on
          val result = statement.executeUpdate(sql)
          assert(result equals 1)
          println(Console.GREEN + "new task added")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally {
          connection.close()
        }
      case None =>
    }
  }

  override def receive: Receive = {
    case AddTask(method) =>
      addTask(method)
  }
}

object DBConnectionTest extends App {
  def uuid: String = {
    java.util.UUID.randomUUID.toString
  }

  val sys = ActorSystem("actor-system")
  val db = sys.actorOf(Props[DBActor], name="db-actor")
  db ! AddTask(uuid)
}
