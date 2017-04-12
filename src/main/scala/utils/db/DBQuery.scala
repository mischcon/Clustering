package utils.db

import java.sql.{Connection, PreparedStatement, Types}

trait DBQuery {
  val table : String
  def perform(connection: Connection) : Any
}

class DBCreateTasksTable(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): Unit = {
    var sql =
      s"CREATE TABLE IF NOT EXISTS $tableName ( " +
       "id            INT(11)      NOT NULL AUTO_INCREMENT, " +
       "method        VARCHAR(128) NOT NULL, " +
      s"task_status   VARCHAR(16)  NOT NULL DEFAULT '${TaskStatus.NOT_STARTED}', " +
       "end_state     VARCHAR(16), " +
       "task_result   VARCHAR(2048), " +
       "started_at    TIMESTAMP DEFAULT 0, " +
       "finished_at   TIMESTAMP DEFAULT 0, " +
       "time_spent    INT(10), " +
       "PRIMARY KEY (id), " +
       "UNIQUE KEY method_UQ (method), " +
       "CONSTRAINT check_task_status CHECK (task_status IN (" +
      s"'${TaskStatus.NOT_STARTED}', '${TaskStatus.RUNNING}', '${TaskStatus.DONE}')), " +
       "CONSTRAINT check_end_state CHECK (end_state IN (" +
      s"NULL, '${EndState.SUCCESS}', '${EndState.FAILURE}', '${EndState.ABANDONED}', '${EndState.ERROR}')));"
    var statement : PreparedStatement = connection.prepareStatement(sql)
    statement.executeUpdate()
    println(s"[DB]: '$tableName' table created")
    sql =
      s"CREATE OR REPLACE TRIGGER ${tableName}_update_timestamps " +
       "BEFORE UPDATE " +
        s"ON clustering.$tableName FOR EACH ROW " +
         "BEGIN " +
           "IF NEW.task_status = 'RUNNING' THEN " +
             "SET NEW.started_at = CURRENT_TIMESTAMP; " +
           "ELSEIF NEW.task_status = 'DONE' THEN " +
             "SET NEW.finished_at = CURRENT_TIMESTAMP; " +
             "SET NEW.time_spent = (SELECT TIMESTAMPDIFF(SECOND, OLD.started_at, CURRENT_TIMESTAMP)); " +
           "END IF; " +
         "END"
    statement = connection.prepareStatement(sql)
    statement.executeUpdate()
    println(s"[DB]: '${tableName}_update_timestamps' trigger created")
  }
}

class DBCountTaskStatus(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): CountedTaskStatus = {
    val sql =
      s"SELECT '${TaskStatus.NOT_STARTED}' AS task_status, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE task_status = '${TaskStatus.NOT_STARTED}' UNION ALL " +
      s"SELECT '${TaskStatus.RUNNING}' AS task_status, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE task_status = '${TaskStatus.RUNNING}' UNION ALL " +
      s"SELECT '${TaskStatus.DONE}' AS task_status, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE task_status = '${TaskStatus.DONE}';"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    val resultSet = statement.executeQuery()
    var result = Map[TaskStatus, Int]()
    while (resultSet.next()) {
      val task_status = TaskStatus.valueOf(resultSet.getString("task_status"))
      val amount = resultSet.getInt("amount")
      result += (task_status -> amount)
    }
    for ((task_status, amount) <- result)
      println(s"[DB]: $task_status - $amount")
    CountedTaskStatus(result)
  }
}

class DBCountEndState(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): CountedEndState = {
    val sql =
      "SELECT end_state, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE end_state IS NULL UNION ALL " +
      s"SELECT '${EndState.SUCCESS}' AS end_state, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE end_state = '${EndState.SUCCESS}' UNION ALL " +
      s"SELECT '${EndState.FAILURE}' AS end_state, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE end_state = '${EndState.FAILURE}' UNION ALL " +
      s"SELECT '${EndState.ABANDONED}' AS end_state, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE end_state = '${EndState.ABANDONED}' UNION ALL " +
      s"SELECT '${EndState.ERROR}' AS end_state, COALESCE(COUNT(*), 0) AS amount " +
      s"FROM $tableName WHERE end_state = '${EndState.ERROR}';"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    val resultSet = statement.executeQuery()
    var result = Map[EndState, Int]()
    while (resultSet.next()) {
      val end_state =
        if (resultSet.getString("end_state") == null)
          EndState.NONE
        else
          EndState.valueOf(resultSet.getString("end_state"))
      val amount = resultSet.getInt("amount")
      result += (end_state -> amount)
    }
    for ((end_state, amount) <- result)
      println(s"[DB]: $end_state - $amount")
    CountedEndState(result)
  }
}

class DBCreateTask(method : String, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    val sql = s"INSERT INTO $tableName (method) VALUES (?);"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, method)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task created")
  }
}

class DBCreateTasks(methods : List[String], tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    var sql = s"INSERT INTO $tableName (method) VALUES ("
    for (i <- 1 to methods.size) sql += "?), ("
    sql = sql.dropRight(3) + ";"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size)
      statement.setString(i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks created")
  }
}

class DBGetTask(method : String, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Option[RequestedTask] = {
    val sql = s"SELECT * FROM $tableName WHERE method = ?;"
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
          if (resultSet.getString("end_state") == null)
            EndState.NONE
          else
            EndState.valueOf(resultSet.getString("end_state"))
        val task_result = resultSet.getString("task_result")
        val started_at = resultSet.getTimestamp("started_at")
        val finished_at = resultSet.getTimestamp("finished_at")
        val time_spent = resultSet.getInt("time_spent")
        task = RequestedTask(method, task_status, end_state, task_result, started_at, finished_at, time_spent)
      }
      println(s"[DB]: task found: ${task.method} - ${task.task_status} - ${task.end_state} - " +
        s"${task.task_result} - ${task.started_at} - ${task.finished_at} - ${task.time_spent}")
      Some(task)
    }
  }
}

class DBGetTasks(methods : List[String], tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Option[List[RequestedTask]] = {
    var sql = s"SELECT * FROM $tableName WHERE method IN ("
    for (i <- 1 to methods.size)
      sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size)
      statement.setString(i, methods(i - 1))
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
          if (resultSet.getString("end_state") == null)
            EndState.NONE
          else
            EndState.valueOf(resultSet.getString("end_state"))
        val task_result = resultSet.getString("task_result")
        val started_at = resultSet.getTimestamp("started_at")
        val finished_at = resultSet.getTimestamp("finished_at")
        val time_spent = resultSet.getInt("time_spent")
        taskList = RequestedTask(method, task_status, end_state, task_result,
          started_at, finished_at, time_spent) :: taskList
      }
      for (task <- taskList)
        println(s"[DB]: task found: ${task.method} - ${task.task_status} - ${task.end_state} - " +
          s"${task.task_result} - ${task.started_at} - ${task.finished_at} - ${task.time_spent}")
      Some(taskList)
    }
  }
}

class DBGetTasksWithStatus(task_status : TaskStatus, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Option[List[RequestedTask]] = {
    val sql = s"SELECT * FROM $tableName WHERE task_status = ?;"
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
          if (resultSet.getString("end_state") == null)
            EndState.NONE
          else
            EndState.valueOf(resultSet.getString("end_state"))
        val task_result = resultSet.getString("task_result")
        val started_at = resultSet.getTimestamp("started_at")
        val finished_at = resultSet.getTimestamp("finished_at")
        val time_spent = resultSet.getInt("time_spent")
        taskList = RequestedTask(method, task_status, end_state, task_result,
          started_at, finished_at, time_spent) :: taskList
      }
      for (task <- taskList)
        println(s"[DB]: task found: ${task.method} - ${task.task_status} - ${task.end_state} - " +
          s"${task.task_result} - ${task.started_at} - ${task.finished_at} - ${task.time_spent}")
      Some(taskList)
    }
  }
}

class DBUpdateTask(method : String, task_status: TaskStatus, end_state: EndState, task_result : String,
                   tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    val sql = s"UPDATE $tableName SET task_status = ?, end_state = ?, task_result = ? WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    if (end_state == EndState.NONE)
      statement.setNull(2, Types.VARCHAR)
    else
      statement.setString(2, end_state.toString)
    statement.setString(3, task_result)
    statement.setString(4, method)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task updated")
  }
}

class DBUpdateTasks(methods : List[String], task_status: TaskStatus, end_state: EndState, task_result : String,
                    tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    var sql = s"UPDATE $tableName SET task_status = ?, end_state = ?, task_result = ? WHERE method IN ("
    for (i <- 1 to methods.size)
      sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    if (end_state == EndState.NONE)
      statement.setNull(2, Types.VARCHAR)
    else
      statement.setString(2, end_state.toString)
    statement.setString(3, task_result)
    for (i <- 1 to methods.size)
      statement.setString(3 + i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks updated")
  }
}

class DBUpdateTaskStatus(method : String, task_status: TaskStatus, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    val sql = s"UPDATE $tableName SET task_status = ? WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    statement.setString(2, method)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task updated")
  }
}

class DBUpdateTasksStatus(methods : List[String], task_status: TaskStatus, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    var sql = s"UPDATE $tableName SET task_status = ? WHERE method IN ("
    for (i <- 1 to methods.size)
      sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    for (i <- 1 to methods.size)
      statement.setString(1 + i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks updated")
  }
}

class DBDeleteTask(method : String, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    val sql = s"DELETE FROM $tableName WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, method.toString)
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task deleted")
  }
}

class DBDeleteTasks(methods : List[String], tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    var sql = s"DELETE FROM $tableName WHERE method IN ("
    for (i <- 1 to methods.size)
      sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size)
      statement.setString(i, methods(i - 1))
    val result = statement.executeUpdate()
    assert(result equals methods.size)
    println(s"[DB]: ${methods.size} tasks deleted")
  }
}