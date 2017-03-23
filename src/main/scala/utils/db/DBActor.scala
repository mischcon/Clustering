package utils.db

import java.sql.{Connection, DriverManager, Statement}
import akka.actor.Actor
import com.typesafe.config.ConfigFactory


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
        println(Console.RED + e.getMessage)
        None
    }
  }

  def createTask(method : String): Unit = {
    connect match {
      case Some(connection) =>
        val statement : Statement = connection.createStatement()
        val sql : String = s"INSERT INTO tasks (method) VALUES ('$method');"
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
        println(Console.RED + "could not connect")
    }
  }

  def readTask(method : String): Unit = {
    connect match {
      case Some(connection) =>
        val statement : Statement = connection.createStatement()
        val sql : String = s"SELECT status, result FROM tasks WHERE method = '$method';"
        try {
          val resultSet = statement.executeQuery(sql)
          if (!resultSet.isBeforeFirst) {
            sender() ! None
            println(Console.RED + s"requested task: $method not found")
          }
          else {
            while (resultSet.next()) {
              val status = TaskStatus.valueOf(resultSet.getString("status"))
              val result = resultSet.getString("result")
              sender() ! Some(RequestedTask(method, status, result))
              println(Console.GREEN + s"responsed w/ requested task:\n[$method - $status - $result]")
            }
          }
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally {
          connection.close()
        }
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  def updateTask(method : String, status : TaskStatus): Unit = {
    connect match {
      case Some(connection) =>
        val statement : Statement = connection.createStatement()
        val sql : String = s"UPDATE tasks SET status = '$status' WHERE method = '$method';"
        try {
          // auto commit is on
          val result = statement.executeUpdate(sql)
          assert(result equals 1)
          println(Console.GREEN + "task updated")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally {
          connection.close()
        }
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  def deleteTask(method : String): Unit = {
    connect match {
      case Some(connection) =>
        val statement : Statement = connection.createStatement()
        val sql : String = s"DELETE FROM tasks WHERE method = '$method';"
        try {
          // auto commit is on
          val result = statement.executeUpdate(sql)
          assert(result equals 1)
          println(Console.GREEN + "task deleted")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally {
          connection.close()
        }
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  override def receive: Receive = {
    case CreateTask(method) =>
      createTask(method)
    case ReadTask(method) =>
      readTask(method)
    case UpdateTask(method, status) =>
      updateTask(method, status)
    case DeleteTask(method) =>
      deleteTask(method)
  }
}
