package utils.db

import java.sql.{Connection, PreparedStatement, Types}

trait DBQuery {
  def perform(connection: Connection) : Any
}

class DBCreateTask(method : String) extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    val sql = "INSERT INTO tasks (method) VALUES (?);"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, method)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task created")
  }
}

class DBCreateTasks(methods : List[String]) extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    var sql = "INSERT INTO tasks (method) VALUES ("
    for (i <- 1 to methods.size) sql += "?), ("
    sql = sql.dropRight(3) + ";"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size) statement.setString(i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks created")
  }
}

class DBGetTask(method : String) extends DBQuery {
  override def perform(connection : Connection) : Option[RequestedTask] = {
    val sql = "SELECT * FROM tasks WHERE method = ?;"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, method)
    val resultSet = statement.executeQuery()
    if (!resultSet.isBeforeFirst) {
      println(s"[DB]: task: $method not found")
      None
    }
    else {
      var task : RequestedTask = null
      while (resultSet.next()) {
        val task_status = TaskStatus.valueOf(resultSet.getString("task_status"))
        val end_state =
          if (resultSet.getString("end_state") == null) EndState.NONE
          else EndState.valueOf(resultSet.getString("end_state"))
        val task_result = resultSet.getString("task_result")
        task = RequestedTask(method, task_status, end_state, task_result)
      }
      println(s"[DB]: task found: ${task.method} - ${task.task_status} - ${task.end_state} - ${task.task_result}")
      Some(task)
    }
  }
}

class DBGetTasks(methods : List[String]) extends DBQuery {
  override def perform(connection : Connection) : Option[List[RequestedTask]] = {
    var sql = "SELECT * FROM tasks WHERE method IN ("
    for (i <- 1 to methods.size) sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size) statement.setString(i, methods(i - 1))
    val resultSet = statement.executeQuery()
    if (!resultSet.isBeforeFirst) {
      println(s"[DB]: no tasks found")
      None
    }
    else {
      var taskList = List[RequestedTask]()
      while (resultSet.next()) {
        val method = resultSet.getString("method")
        val task_status = TaskStatus.valueOf(resultSet.getString("task_status"))
        val end_state =
          if (resultSet.getString("end_state") == null) EndState.NONE
          else EndState.valueOf(resultSet.getString("end_state"))
        val task_result = resultSet.getString("task_result")
        taskList = RequestedTask(method, task_status, end_state, task_result) :: taskList
      }
      for (task <- taskList)
        println(s"[DB]: task found: ${task.method} - ${task.task_status} - ${task.end_state} - ${task.task_result}")
      Some(taskList)
    }
  }
}

class DBGetTasksWithStatus(task_status : TaskStatus) extends DBQuery {
  override def perform(connection : Connection) : Option[List[RequestedTask]] = {
    val sql = s"SELECT * FROM tasks WHERE task_status = ?;"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    val resultSet = statement.executeQuery()
    if (!resultSet.isBeforeFirst) {
      println(s"[DB]: no tasks w/ status $task_status found")
      None
    }
    else {
      var taskList = List[RequestedTask]()
      while (resultSet.next()) {
        val method = resultSet.getString("method")
        val end_state =
          if (resultSet.getString("end_state") == null) EndState.NONE
          else EndState.valueOf(resultSet.getString("end_state"))
        val task_result = resultSet.getString("task_result")
        taskList = RequestedTask(method, task_status, end_state, task_result) :: taskList
      }
      for (task <- taskList)
        println(s"[DB]: task found: ${task.method} - ${task.task_status} - ${task.end_state} - ${task.task_result}")
      Some(taskList)
    }
  }
}

class DBUpdateTask(method : String, task_status: TaskStatus, end_state: EndState, task_result : String)
  extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    val sql : String = "UPDATE tasks SET task_status = ?, end_state = ?, task_result = ? WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    if (end_state == EndState.NONE) statement.setNull(2, Types.VARCHAR) else statement.setString(2, end_state.toString)
    statement.setString(3, task_result)
    statement.setString(4, method)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task updated")
  }
}

class DBUpdateTasks(methods : List[String], task_status: TaskStatus, end_state: EndState, task_result : String)
  extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    var sql = "UPDATE tasks SET task_status = ?, end_state = ?, task_result = ? WHERE method IN ("
    for (i <- 1 to methods.size) sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    if (end_state == EndState.NONE) statement.setNull(2, Types.VARCHAR) else statement.setString(2, end_state.toString)
    statement.setString(3, task_result)
    for (i <- 1 to methods.size) statement.setString(3 + i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks updated")
  }
}

class DBUpdateTaskStatus(method : String, task_status: TaskStatus) extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    val sql = "UPDATE tasks SET task_status = ? WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    statement.setString(2, method)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task updated")
  }
}

class DBUpdateTasksStatus(methods : List[String], task_status: TaskStatus) extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    var sql = "UPDATE tasks SET task_status = ? WHERE method IN ("
    for (i <- 1 to methods.size) sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    for (i <- 1 to methods.size) statement.setString(1 + i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks updated")
  }
}

class DBDeleteTask(method : String) extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    val sql = "DELETE FROM tasks WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, method.toString)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task deleted")
  }
}

class DBDeleteTasks(methods : List[String]) extends DBQuery {
  override def perform(connection : Connection) : Unit = {
    var sql = "DELETE FROM tasks WHERE method IN ("
    for (i <- 1 to methods.size) sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size) statement.setString(i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks deleted")
  }
}