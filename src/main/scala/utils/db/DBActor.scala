package utils.db

import java.sql.{Connection, DriverManager, PreparedStatement, Types}

import akka.actor.Actor
import com.typesafe.config.ConfigFactory

/**
  * = Actor-based interface between cluster actors & database =
  *{{{
  * Current database scheme:
  *
  * tasks:
  * +-----+--------+-------------+-----------+-------------+
  * | id  | method | task_status | end_state | task_result |
  * +-----+--------+-------------+-----------+-------------+
  * | int | string | NOT_STARTED | SUCCESS   | string      |
  * |     |        | IN_PROGRESS | FAILURE   |             |
  * |     |        | DONE        | ERROR     |             |
  * +-----+--------+-------------+-----------+-------------+
  * }}}
  * All messages that are meant to be sent to this actor use [[utils.db.DBMessage]].
  */
class DBActor extends Actor {

  /**
    * = Connects to the configured database =
    * Configuration is found @ ''application.conf'' @ section ''db''
    * @return [[java.sql.Connection]] Object or [[scala.None]]
    */
  def connect: Option[Connection] = try {
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

  /**
    * = Creates task entry in the database =
    * @param method name of the task to be saved; __must be unique__
    */
  def createTask(method : String): Unit = {
    connect match {
      case Some(connection) =>
        val sql = "INSERT INTO tasks (method) VALUES (?);"
        val statement : PreparedStatement = connection.prepareStatement(sql)
        statement.setString(1, method)
        try {
          val result = statement.executeUpdate()
          assert(result equals 1)
          println(Console.GREEN + "task created")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Creates several task entries in the database =
    * @param methods list w/ names of tasks to be saved; __names must be unique__
    */
  def createTasks(methods : List[String]): Unit = {
    connect match {
      case Some(connection) =>
        val statement = connection.createStatement()
        var sql = "INSERT INTO tasks (method) VALUES"
        for (method <- methods) {
          sql += s" ('$method'),"
        }
        sql = sql.dropRight(1) + ";"
        try {
          val result = statement.executeUpdate(sql)
          assert(result equals methods.size)
          println(Console.GREEN + s"${methods.size} tasks created")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Answers w/ requested task =
    * Some([[utils.db.RequestedTask]]) or [[scala.None]]
    * @param method name of requested task
    */
  def getTask(method : String): Unit = {
    connect match {
      case Some(connection) =>
        val sql = "SELECT * FROM tasks WHERE method = ?;"
        val statement : PreparedStatement = connection.prepareStatement(sql)
        statement.setString(1, method)
        try {
          val resultSet = statement.executeQuery()
          if (!resultSet.isBeforeFirst) {
            sender() ! None
            println(Console.RED + s"requested task: $method not found")
          }
          else while (resultSet.next()) {
            val task_status = TaskStatus.valueOf(resultSet.getString("task_status"))
            val end_state = if (resultSet.getString("end_state") == null) EndState.NONE else EndState.valueOf(resultSet.getString("end_state"))
            val task_result = resultSet.getString("task_result")
            sender() ! Some(RequestedTask(method, task_status, EndState.NONE, task_result))
            println(Console.GREEN + s"responsed w/ requested task:\n$method - $task_status - $end_state - $task_result")
          }
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getCause)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Answers w/ requested tasks =
    * Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]]
    * @param methods list w/ names of requested tasks
    */
  def getTasks(methods : List[String]): Unit = {
    connect match {
      case Some(connection) =>
        var sql = "SELECT * FROM tasks WHERE method IN ("
        for (i <- 1 to methods.size)
          sql += "?, "
        sql = sql.dropRight(2) + ");"
        val statement : PreparedStatement = connection.prepareStatement(sql)
        for (i <- 1 to methods.size)
          statement.setString(i, methods(i - 1))
        try {
          val resultSet = statement.executeQuery()
          if (!resultSet.isBeforeFirst) {
            sender() ! None
            println(Console.RED + s"requested tasks not found")
          }
          else {
            var taskList = List[RequestedTask]()
            while (resultSet.next()) {
              val method = resultSet.getString("method")
              val task_status = TaskStatus.valueOf(resultSet.getString("task_status"))
              val end_state = if (resultSet.getString("end_state") == null) EndState.NONE else EndState.valueOf(resultSet.getString("end_state"))
              val task_result = resultSet.getString("task_result")
              taskList = RequestedTask(method, task_status, end_state, task_result) :: taskList
              println(Console.GREEN + s"responsed w/ requested task:\n$method - $task_status - $end_state - $task_result")
            }
            sender() ! Some(taskList)
          }
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Answers w/ requested tasks =
    * Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]]
    * @param task_status requested status from [[utils.db.TaskStatus]]
    */
  def getTasksWithStatus(task_status: TaskStatus): Unit = {
    connect match {
      case Some(connection) =>
        val sql = s"SELECT * FROM tasks WHERE task_status = ?;"
        val statement : PreparedStatement = connection.prepareStatement(sql)
        statement.setString(1, task_status.toString)
        try {
          val resultSet = statement.executeQuery()
          if (!resultSet.isBeforeFirst) {
            sender() ! None
            println(Console.RED + s"no tasks w/ status $task_status found")
          }
          else {
            var taskList = List[RequestedTask]()
            while (resultSet.next()) {
              val method = resultSet.getString("method")
              val end_state = if (resultSet.getString("end_state") == null) EndState.NONE else EndState.valueOf(resultSet.getString("end_state"))
              val task_result = resultSet.getString("task_result")
              taskList = RequestedTask(method, task_status, end_state, task_result) :: taskList
              println(Console.GREEN + s"responsed w/ requested task:\n$method - $task_status - $end_state - $task_result")
            }
            sender() ! Some(taskList)
          }
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Updates task w/ new values =
    * @param method name of task to update
    * @param task_status new status from [[utils.db.TaskStatus]]
    * @param end_state new end_state from [[utils.db.EndState]]
    * @param task_result new result
    */
  def updateTask(method : String, task_status : TaskStatus, end_state : EndState, task_result : String): Unit = {
    connect match {
      case Some(connection) =>
        val sql : String = "UPDATE tasks SET task_status = ?, end_state = ?, task_result = ? WHERE method = ?;"
        val statement : PreparedStatement  = connection.prepareStatement(sql)
        statement.setString(1, task_status.toString)
        if (end_state == EndState.NONE) statement.setNull(2, Types.VARCHAR) else statement.setString(2, end_state.toString)
        statement.setString(3, task_result)
        statement.setString(4, method)
        try {
          val result = statement.executeUpdate()
          assert(result equals 1)
          println(Console.GREEN + "task updated")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Updates several tasks w/ new values =
    * @param methods list w/ names of tasks to update
    * @param task_status new status for all tasks from [[utils.db.TaskStatus]]
    * @param end_state new end_state for all tasks from [[utils.db.EndState]]
    * @param task_result new result for all tasks
    */
  def updateTasks(methods : List[String], task_status : TaskStatus, end_state : EndState, task_result : String): Unit = {
    connect match {
      case Some(connection) =>
        var sql = "UPDATE tasks SET task_status = ?, end_state = ?, task_result = ? WHERE method IN ("
        for (i <- 1 to methods.size)
          sql += "?, "
        sql = sql.dropRight(2) + ");"
        val statement : PreparedStatement = connection.prepareStatement(sql)
        statement.setString(1, task_status.toString)
        if (end_state == EndState.NONE) statement.setNull(2, Types.VARCHAR) else statement.setString(2, end_state.toString)
        statement.setString(3, task_result)
        for (i <- 1 to methods.size)
          statement.setString(3 + i, methods(i - 1))
        try {
          val result = statement.executeUpdate()
          assert(result equals methods.size)
          println(Console.GREEN + s"${methods.size} tasks updated")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Updates task w/ new status =
    * @param method name of task to update
    * @param task_status new status from [[utils.db.TaskStatus]]
    */
  def updateTaskStatus(method : String, task_status : TaskStatus): Unit = {
    connect match {
      case Some(connection) =>
        val sql = "UPDATE tasks SET task_status = ? WHERE method = ?;"
        val statement : PreparedStatement  = connection.prepareStatement(sql)
        statement.setString(1, task_status.toString)
        statement.setString(2, method)
        try {
          val result = statement.executeUpdate()
          assert(result equals 1)
          println(Console.GREEN + "task updated")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Updates several tasks w/ new status =
    * @param methods list w/ names of tasks to update
    * @param task_status new status for all tasks from [[utils.db.TaskStatus]]
    */
  def updateTasksStatus(methods : List[String], task_status : TaskStatus): Unit = {
    connect match {
      case Some(connection) =>
        var sql = "UPDATE tasks SET task_status = ? WHERE method IN ("
        for (i <- 1 to methods.size)
          sql += "?, "
        sql = sql.dropRight(2) + ");"
        val statement : PreparedStatement = connection.prepareStatement(sql)
        statement.setString(1, task_status.toString)
        for (i <- 1 to methods.size)
          statement.setString(1 + i, methods(i - 1))
        try {
          val result = statement.executeUpdate()
          assert(result equals methods.size)
          println(Console.GREEN + s"${methods.size} tasks updated")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Deletes requested task =
    * @param method name of task to delete
    */
  def deleteTask(method : String): Unit = {
    connect match {
      case Some(connection) =>
        val sql = "DELETE FROM tasks WHERE method = ?;"
        val statement : PreparedStatement  = connection.prepareStatement(sql)
        statement.setString(1, method.toString)
        try {
          val result = statement.executeUpdate()
          assert(result equals 1)
          println(Console.GREEN + "task deleted")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
      case None =>
        println(Console.RED + "could not connect")
    }
  }

  /**
    * = Deletes requested tasks =
    * @param methods list w/ names of tasks to delete
    */
  def deleteTasks(methods : List[String]): Unit = {
    connect match {
      case Some(connection) =>
        var sql = "DELETE FROM tasks WHERE method IN ("
        for (i <- 1 to methods.size)
          sql += "?, "
        sql = sql.dropRight(2) + ");"
        val statement : PreparedStatement = connection.prepareStatement(sql)
        for (i <- 1 to methods.size)
          statement.setString(i, methods(i - 1))
        try {
          val result = statement.executeUpdate()
          assert(result equals methods.size)
          println(Console.GREEN + s"${methods.size} tasks deleted")
        }
        catch {
          case e: Exception =>
            println(Console.RED + e.getMessage)
        }
        finally connection.close()
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
    case GetTasks(methods) =>
      getTasks(methods)
    case GetTasksWithStatus(task_status) =>
      getTasksWithStatus(task_status)
    case UpdateTask(method, task_status, end_state, task_result) =>
      updateTask(method, task_status, end_state, task_result)
    case UpdateTasks(methods, task_status, end_state, task_result) =>
      updateTasks(methods, task_status, end_state, task_result)
    case UpdateTaskStatus(method, task_status) =>
      updateTaskStatus(method, task_status)
    case UpdateTasksStatus(methods, task_status) =>
      updateTasksStatus(methods, task_status)
    case DeleteTask(method) =>
      deleteTask(method)
    case DeleteTasks(methods) =>
      deleteTasks(methods)
  }
}
