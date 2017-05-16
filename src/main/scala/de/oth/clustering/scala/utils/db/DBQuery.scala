package de.oth.clustering.scala.utils.db

import java.io.{File, PrintWriter}
import java.sql.{Connection, PreparedStatement, Types}


trait DBQuery {
  val table : String
  def perform(connection: Connection) : Any
}

class DBGetTables extends DBQuery {
  override val table: String = ""
  override def perform(connection: Connection): Tables = {
    val sql = "SELECT table_name FROM information_schema.tables WHERE table_schema='de.oth.clustering.java.clustering' ORDER BY table_name DESC;"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    val resultSet = statement.executeQuery()
    var tables = List[String]()
    while (resultSet.next()) {
      val tableName = resultSet.getString("table_name")
      tables = tableName :: tables
    }
    Tables(tables)
  }
}

class DBCreateTasksTable(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): Unit = {
    var sql =
      s"CREATE TABLE IF NOT EXISTS $tableName ( " +
       "id            INT(11)      NOT NULL AUTO_INCREMENT, " +
       "method        VARCHAR(128) NOT NULL, " +
       "params        VARCHAR(512), " +
      s"task_status   VARCHAR(16)  NOT NULL DEFAULT '${TaskStatus.NOT_STARTED}', " +
       "end_state     VARCHAR(16), " +
       "task_result   VARCHAR(2048), " +
       "started_at    TIMESTAMP DEFAULT 0, " +
       "finished_at   TIMESTAMP DEFAULT 0, " +
       "time_spent    INT(10), " +
       "PRIMARY KEY (id), " +
       "UNIQUE KEY method_UQ (method));"
    var statement : PreparedStatement = connection.prepareStatement(sql)
    statement.executeUpdate()
    sql =
      s"CREATE OR REPLACE TRIGGER ${tableName}_update_timestamps " +
       "BEFORE UPDATE " +
        s"ON de.oth.clustering.java.clustering.$tableName FOR EACH ROW " +
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
  }
}

class DBGenerateTextReport(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): Report = {
    val query1 = new DBGetTasksWithStatus(TaskStatus.DONE, tableName)
    val doneTasks = query1.perform(connection)
    val query2 = new DBCountEndState(tableName)
    val endStateOfTasks = query2.perform(connection)
    var path = ""
    doneTasks match {
      case Some(tasks) =>
        val tmp = File.createTempFile(java.util.UUID.randomUUID.toString, ".txt")
        tmp.deleteOnExit()
        path = tmp.getAbsolutePath
        val w = new PrintWriter(tmp)
        val sb = new StringBuilder
        sb.append(s"TASK SET : $tableName\n\n")
        for ((k, v) <- endStateOfTasks.result) {
          if (k == EndState.ABANDONED)
            sb.append(s"$k\t$v\n")
          else
            sb.append(s"$k\t\t$v\n")
        }
        sb.append("\n")
        for (task <- tasks) {
          sb.append("===================================================================================================\n")
          sb.append(task.end_state.toString + "\n\n")
          var paramsAsString = ""
          for ((name, value) <- task.params)
            paramsAsString += name + "=" + value + "; "
          paramsAsString dropRight 1
          sb.append(s"METHOD     : ${task.method}($paramsAsString)\n")
          sb.append(s"STARTED @  : ${task.started_at}\n")
          sb.append(s"FINISHED @ : ${task.finished_at}\n")
          sb.append(s"TIME SPENT : ${task.time_spent} SEC\n")
          sb.append(s"RESULT :\n${task.task_result}\n")
        }
        sb.append("===================================================================================================")
        w.write(sb.toString)
        w.close()
      case None =>

    }
    Report(path)
  }
}

class DBGenerateJsonReport(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): Report = {
    val query1 = new DBGetTasksWithStatus(TaskStatus.DONE, tableName)
    val doneTasks = query1.perform(connection)
    var path = ""
    doneTasks match {
      case Some(tasks) =>
        val tmp = File.createTempFile(java.util.UUID.randomUUID.toString, ".json")
        tmp.deleteOnExit()
        path = tmp.getAbsolutePath
        val w = new PrintWriter(tmp)
        var jsonString = """{
  "data": ["""
        for (task <- tasks) {
          var paramsAsString = ""
          for ((name, value) <- task.params)
            paramsAsString += name + "=" + value + "; "
          paramsAsString = paramsAsString dropRight 1
          val s = task.time_spent
          val m = (s/60) % 60
          val h = (s/60/60) % 24
          val timeString = "%02d:%02d:%02d".format(h, m, s)
          jsonString += s"""
    {
      "end_state": "${task.end_state}",
      "method": "${task.method}($paramsAsString)",
      "started_at": "${task.started_at}",
      "finished_at": "${task.finished_at}",
      "time_spent": "$timeString",
      "result": "${task.task_result}"
    },"""
        }
        jsonString = jsonString dropRight 1
        jsonString += """
  ]
}"""
        w.write(jsonString)
        w.close()
      case None =>

    }
    Report(path)
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
    CountedEndState(result)
  }
}

class DBCreateTask(method : String, tableName : String) extends DBQuery {
  override val table: String = tableName
  var params : Map[String, String] = _

  // for parametrized tasks
  def this(method : String, params : Map[String, String], tableName: String) {
    this(method, tableName)
    this.params = params
  }

  override def perform(connection : Connection) : Unit = {
    var statement : PreparedStatement = null
    if (params eq null) {
      val sql = s"INSERT INTO $tableName (method) VALUES (?);"
      statement = connection.prepareStatement(sql)
      statement.setString(1, method)
    }
    // parametrized tasks
    else {
      val sql = s"INSERT INTO $tableName (method, params) VALUES (?, ?);"
      statement = connection.prepareStatement(sql)
      statement.setString(1, method)
      var paramsAsString = ""
      for ((name, value) <- params)
        paramsAsString += name + "=" + value + "; "
      paramsAsString dropRight 1
      statement.setString(2, paramsAsString)
    }
    statement.executeUpdate()
  }
}

class DBCreateTasks(methods : List[String], tableName : String) extends DBQuery {
  override val table: String = tableName
  var methodsWithParams : Map[String, Map[String, String]] = _

  // for parametrized tasks
  def this(methodsWithParams : Map[String, Map[String, String]], tableName: String) {
    this(methodsWithParams.keys.toList, tableName)
    this.methodsWithParams = methodsWithParams
  }

  override def perform(connection : Connection) : Unit = {
    var statement : PreparedStatement = null
    if (methodsWithParams eq null) {
      var sql = s"INSERT INTO $tableName (method) VALUES ("
      for (_ <- 1 to methods.size) sql += "?), ("
      sql = sql.dropRight(3) + ";"
      statement = connection.prepareStatement(sql)
      for (i <- 1 to methods.size)
        statement.setString(i, methods(i - 1))
    }
    // parametrized tasks
    else {
      var sql = s"INSERT INTO $tableName (method, params) VALUES ("
      for (_ <- 1 to methods.size) sql += "?, ?), ("
      sql = sql.dropRight(3) + ";"
      statement = connection.prepareStatement(sql)
      var i = 1
      var j = 2
      for (k <- 1 to methods.size) {
        statement.setString(i, methods(k - 1))
        var paramsAsString = ""
        for ((name, value) <- methodsWithParams(methods(k - 1)))
          paramsAsString += name + "=" + value + "; "
        paramsAsString dropRight 1
        statement.setString(j, paramsAsString)
        i += 2
        j += 2
      }
    }
    statement.executeUpdate()
  }
}

class DBGetTask(method : String, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Option[RequestedTask] = {
    val sql = s"SELECT * FROM $tableName WHERE method = ?;"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, method)
    val resultSet = statement.executeQuery()
    if (!resultSet.isBeforeFirst)
      None
    else {
      var task : RequestedTask = null
      var paramsstr = ""
      while (resultSet.next()) {
        var params = Map[String, String]()
        if (resultSet.getString("params") != null) {
          paramsstr = resultSet.getString("params")
          val pairs = resultSet.getString("params").replace(";", "").split("=| ").grouped(2)
          params = pairs.map { case Array(k, v) => k -> v }.toMap
        }
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
        task = RequestedTask(method, params, task_status, end_state, task_result, started_at, finished_at, time_spent)
      }
      Some(task)
    }
  }
}

class DBGetTasks(methods : List[String], tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Option[List[RequestedTask]] = {
    var sql = s"SELECT * FROM $tableName WHERE method IN ("
    for (_ <- 1 to methods.size)
      sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size)
      statement.setString(i, methods(i - 1))
    val resultSet = statement.executeQuery()
    if (!resultSet.isBeforeFirst)
      None
    else {
      var taskList = List[RequestedTask]()
      var paramsstr = ""
      while (resultSet.next()) {
        val method = resultSet.getString("method")
        var params = Map[String, String]()
        if (resultSet.getString("params") != null) {
          paramsstr = resultSet.getString("params")
          val pairs = resultSet.getString("params").replace(";", "").split("=| ").grouped(2)
          params = pairs.map { case Array(k, v) => k -> v }.toMap
        }
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
        taskList = RequestedTask(method, params, task_status, end_state, task_result,
          started_at, finished_at, time_spent) :: taskList
      }
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
    if (!resultSet.isBeforeFirst)
      None
    else {
      var taskList = List[RequestedTask]()
      var paramsstr = ""
      while (resultSet.next()) {
        val method = resultSet.getString("method")
        var params = Map[String, String]()
        if (resultSet.getString("params") != null) {
          paramsstr = resultSet.getString("params")
          val pairs = resultSet.getString("params").replace(";", "").split("=| ").grouped(2)
          params = pairs.map { case Array(k, v) => k -> v }.toMap
        }
        val end_state =
          if (resultSet.getString("end_state") == null)
            EndState.NONE
          else
            EndState.valueOf(resultSet.getString("end_state"))
        val task_result = resultSet.getString("task_result")
        val started_at = resultSet.getTimestamp("started_at")
        val finished_at = resultSet.getTimestamp("finished_at")
        val time_spent = resultSet.getInt("time_spent")
        taskList = RequestedTask(method, params, task_status, end_state, task_result,
          started_at, finished_at, time_spent) :: taskList
      }
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
    statement.executeUpdate()
  }
}

class DBUpdateTasks(methods : List[String], task_status: TaskStatus, end_state: EndState, task_result : String,
                    tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    var sql = s"UPDATE $tableName SET task_status = ?, end_state = ?, task_result = ? WHERE method IN ("
    for (_ <- 1 to methods.size)
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
    statement.executeUpdate()
  }
}

class DBUpdateTaskStatus(method : String, task_status: TaskStatus, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    val sql = s"UPDATE $tableName SET task_status = ? WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    statement.setString(2, method)
    statement.executeUpdate()
  }
}

class DBUpdateTasksStatus(methods : List[String], task_status: TaskStatus, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    var sql = s"UPDATE $tableName SET task_status = ? WHERE method IN ("
    for (_ <- 1 to methods.size)
      sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    statement.setString(1, task_status.toString)
    for (i <- 1 to methods.size)
      statement.setString(1 + i, methods(i - 1))
    statement.executeUpdate()
  }
}

class DBDeleteTask(method : String, tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    val sql = s"DELETE FROM $tableName WHERE method = ?;"
    val statement : PreparedStatement  = connection.prepareStatement(sql)
    statement.setString(1, method.toString)
    statement.executeUpdate()
  }
}

class DBDeleteTasks(methods : List[String], tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection : Connection) : Unit = {
    var sql = s"DELETE FROM $tableName WHERE method IN ("
    for (_ <- 1 to methods.size)
      sql += "?, "
    sql = sql.dropRight(2) + ");"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    for (i <- 1 to methods.size)
      statement.setString(i, methods(i - 1))
    statement.executeUpdate()
  }
}