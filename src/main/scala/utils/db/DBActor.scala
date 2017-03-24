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

  def createTasks(methods : List[String]): Unit = {
    connect match {
      case Some(connection) =>
        val statement : Statement = connection.createStatement()
        var sql : String = s"INSERT INTO tasks (method) VALUES"
        for (method <- methods) {
          sql += s" ('$method'),"
        }
        sql = sql.dropRight(1) + ";"
        try {
          // auto commit is on
          val result = statement.executeUpdate(sql)
          assert(result equals methods.size)
          println(Console.GREEN + "new tasks added")
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

  def getTask(method : String): Unit = {
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
              println(Console.GREEN + s"responsed w/ requested task:\n$method - $status - $result")
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

  def getTasksWithStatus(status: TaskStatus): Unit = {
    connect match {
      case Some(connection) =>
        val statement : Statement = connection.createStatement()
        val sql : String = s"SELECT method, result FROM tasks WHERE status = '$status';"
        try {
          val resultSet = statement.executeQuery(sql)
          if (!resultSet.isBeforeFirst) {
            sender() ! None
            println(Console.RED + s"no tasks w/ status $status found")
          }
          else {
            var taskList = List[RequestedTask]()
            while (resultSet.next()) {
              val method = resultSet.getString("method")
              val result = resultSet.getString("result")
              taskList = RequestedTask(method, status, result) :: taskList
              println(Console.GREEN + s"responsed w/ requested task:\n$method - $status - $result")
            }
            sender() ! Some(taskList)
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
    case CreateTasks(methods) =>
      createTasks(methods)
    case GetTask(method) =>
      getTask(method)
    case GetTasksWithStatus(status) =>
      getTasksWithStatus(status)
    case UpdateTaskStatus(method, status) =>
      updateTask(method, status)
    case DeleteTask(method) =>
      deleteTask(method)
  }
}
