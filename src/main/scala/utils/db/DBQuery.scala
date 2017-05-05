package utils.db

import java.io.{BufferedWriter, File, FileWriter}
import java.sql.{Connection, PreparedStatement, Types}


trait DBQuery {
  val table : String
  def perform(connection: Connection) : Any
}

class DBGetTables extends DBQuery {
  override val table: String = ""
  override def perform(connection: Connection): Tables = {
    val sql = "SELECT table_name FROM information_schema.tables WHERE table_schema='clustering' ORDER BY table_name ASC;"
    val statement : PreparedStatement = connection.prepareStatement(sql)
    val resultSet = statement.executeQuery()
    var tables = List[String]()
    while (resultSet.next()) {
      val tableName = resultSet.getString("table_name")
      tables = tableName :: tables
    }
    for (table <- tables)
      println(s"[DB]: table - $table")
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
    println(s"[DB]: '$tableName' table created")
    sql =
      s"CREATE TRIGGER ${tableName}_update_timestamps " +
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

class DBGenerateTextReport(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): Unit = {
    val query1 = new DBGetTasksWithStatus(TaskStatus.DONE, tableName)
    val doneTasks = query1.perform(connection)
    val query2 = new DBCountEndState(tableName)
    val endStateOfTasks = query2.perform(connection)
    doneTasks match {
      case Some(tasks) =>
        val file = new File(s"$tableName.txt")
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(s"TASK SET : $tableName\n\n")
        for ((k, v) <- endStateOfTasks.result) {
          if (k == EndState.ABANDONED)
            bw.write(s"$k\t$v\n")
          else
            bw.write(s"$k\t\t$v\n")
        }
        bw.write("\n")
        for (task <- tasks) {
          bw.write("===================================================================================================\n")
          bw.write(task.end_state.toString + "\n\n")
          var paramsAsString = ""
          for ((name, value) <- task.params)
            paramsAsString += name + "=" + value + "; "
          paramsAsString dropRight 1
          bw.write(s"METHOD     : ${task.method} ($paramsAsString)\n")
          bw.write(s"STARTED @  : ${task.started_at}\n")
          bw.write(s"FINISHED @ : ${task.finished_at}\n")
          bw.write(s"TIME SPENT : ${task.time_spent} SEC\n")
          bw.write(s"RESULT :\n${task.task_result}\n")
        }
        bw.write("===================================================================================================")
        bw.close()
        println(s"[DB]: text report generated")
      case None =>
        println(s"[DB]: nothing to generate")
    }
  }
}

class DBGenerateHtmlReport(tableName : String) extends DBQuery {
  override val table: String = tableName
  override def perform(connection: Connection): HtmlReport = {
    val query1 = new DBGetTasksWithStatus(TaskStatus.DONE, tableName)
    val doneTasks = query1.perform(connection)
    val query2 = new DBCountEndState(tableName)
    val endStateOfTasks = query2.perform(connection)
    doneTasks match {
      case Some(tasks) =>
        var html = "<style type='text/css'>" +
          ".tg  {border-collapse:collapse;border-spacing:0;border-color:#ccc;}" +
          ".tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#fff;}" +
          ".tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#f0f0f0;}" +
          ".tg .tg-q9ef{font-weight:bold;font-size:14px;font-family:'Lucida Console', Monaco, monospace !important;;text-align:center;vertical-align:top}" +
          ".tg .tg-baqh{text-align:center;vertical-align:top}" +
          ".tg .tg-dheh{font-weight:bold;font-size:14px;font-family:'Lucida Console', Monaco, monospace !important;;vertical-align:top}" +
          ".tg .tg-yw4l{vertical-align:top}" +
          "th.tg-sort-header::-moz-selection { background:transparent; }th.tg-sort-header::selection      { background:transparent; }th.tg-sort-header { cursor:pointer; }table th.tg-sort-header:after {  content:'';  float:right;  margin-top:7px;  border-width:0 4px 4px;  border-style:solid;  border-color:#404040 transparent;  visibility:hidden;  }table th.tg-sort-header:hover:after {  visibility:visible;  }table th.tg-sort-desc:after,table th.tg-sort-asc:after,table th.tg-sort-asc:hover:after {  visibility:visible;  opacity:0.4;  }table th.tg-sort-desc:after {  border-bottom:none;  border-width:4px 4px 0;  }@media screen and (max-width: 767px) {.tg {width: auto !important;}.tg col {width: auto !important;}.tg-wrap {overflow-x: auto;-webkit-overflow-scrolling: touch;}}</style>" +
          "<div class='tg-wrap'><table id='tg-wmeR8' class='tg'>" +
          "  <tr>" +
          "    <th class='tg-dheh'>status</th>" +
          "    <th class='tg-dheh'>method</th>" +
          "    <th class='tg-q9ef'>started at</th>" +
          "    <th class='tg-q9ef'>finished at</th>" +
          "    <th class='tg-q9ef'>time spent</th>" +
          "    <th class='tg-dheh'>result</th>" +
          "  </tr>"
        for (task <- tasks) {
          html += "  <tr>" +
            s"    <td class='tg-dheh'>${task.task_status}</td>" +
            s"    <td class='tg-yw4l'>${task.method}</td>" +
            s"    <td class='tg-baqh'>${task.started_at}</td>" +
            s"    <td class='tg-baqh'>${task.finished_at}</td>" +
            s"    <td class='tg-baqh'>${task.time_spent}</td>" +
            s"    <td class='tg-yw4l'>${task.task_result}</td>" +
            "  </tr>"
        }
        html += "</table></div>" +
          "<script type='text/javascript' charset='utf-8'>var TgTableSort=window.TgTableSort||function(n,t){'use strict';function r(n,t){for(var e=[],o=n.childNodes,i=0;i<o.length;++i){var u=o[i];if('.'==t.substring(0,1)){var a=t.substring(1);f(u,a)&&e.push(u)}else u.nodeName.toLowerCase()==t&&e.push(u);var c=r(u,t);e=e.concat(c)}return e}function e(n,t){var e=[],o=r(n,'tr');return o.forEach(function(n){var o=r(n,'td');t>=0&&t<o.length&&e.push(o[t])}),e}function o(n){return n.textContent||n.innerText||''}function i(n){return n.innerHTML||''}function u(n,t){var r=e(n,t);return r.map(o)}function a(n,t){var r=e(n,t);return r.map(i)}function c(n){var t=n.className||'';return t.match(/\\S+/g)||[]}function f(n,t){return-1!=c(n).indexOf(t)}function s(n,t){f(n,t)||(n.className+=' '+t)}function d(n,t){if(f(n,t)){var r=c(n),e=r.indexOf(t);r.splice(e,1),n.className=r.join(' ')}}function v(n){d(n,L),d(n,E)}function l(n,t,e){r(n,'.'+E).map(v),r(n,'.'+L).map(v),e==T?s(t,E):s(t,L)}function g(n){return function(t,r){var e=n*t.str.localeCompare(r.str);return 0==e&&(e=t.index-r.index),e}}function h(n){return function(t,r){var e=+t.str,o=+r.str;return e==o?t.index-r.index:n*(e-o)}}function m(n,t,r){var e=u(n,t),o=e.map(function(n,t){return{str:n,index:t}}),i=e&&-1==e.map(isNaN).indexOf(!0),a=i?h(r):g(r);return o.sort(a),o.map(function(n){return n.index})}function p(n,t,r,o){for(var i=f(o,E)?N:T,u=m(n,r,i),c=0;t>c;++c){var s=e(n,c),d=a(n,c);s.forEach(function(n,t){n.innerHTML=d[u[t]]})}l(n,o,i)}function x(n,t){var r=t.length;t.forEach(function(t,e){t.addEventListener('click',function(){p(n,r,e,t)}),s(t,'tg-sort-header')})}var T=1,N=-1,E='tg-sort-asc',L='tg-sort-desc';return function(t){var e=n.getElementById(t),o=r(e,'tr'),i=o.length>0?r(o[0],'td'):[];0==i.length&&(i=r(o[0],'th'));for(var u=1;u<o.length;++u){var a=r(o[u],'td');if(a.length!=i.length)return}x(e,i)}}(document);document.addEventListener('DOMContentLoaded',function(n){TgTableSort('tg-wmeR8')});</script>"

//        val sb = new StringBuilder
//        sb.append(s"TASK SET : $tableName\n\n")
//        for ((k, v) <- endStateOfTasks.result) {
//          if (k == EndState.ABANDONED)
//            sb.append(s"$k\t$v\n")
//          else
//            sb.append(s"$k\t\t$v\n")
//        }
//        sb.append("\n")
//        for (task <- tasks) {
//          sb.append("===================================================================================================\n")
//          sb.append(task.end_state.toString + "\n\n")
//          var paramsAsString = ""
//          for ((name, value) <- task.params)
//            paramsAsString += name + "=" + value + "; "
//          paramsAsString dropRight 1
//          sb.append(s"METHOD     : ${task.method} ($paramsAsString)\n")
//          sb.append(s"STARTED @  : ${task.started_at}\n")
//          sb.append(s"FINISHED @ : ${task.finished_at}\n")
//          sb.append(s"TIME SPENT : ${task.time_spent} SEC\n")
//          sb.append(s"RESULT :\n${task.task_result}\n")
//        }
//        sb.append("===================================================================================================")
        println(s"[DB]: text report generated")
        HtmlReport(html)
//        HtmlReport(sb.toString)
      case None =>
        println(s"[DB]: nothing to generate")
        HtmlReport("nothing to generate")
    }
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
    val result = statement.executeUpdate()
    assert(result equals 1)
    println(s"[DB]: task created")
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
      println(s"[DB]: task found: ${task.method} - $paramsstr - ${task.task_status} - " +
        s"${task.end_state} - ${task.task_result} - ${task.started_at} - ${task.finished_at} - ${task.time_spent}")
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
    if (!resultSet.isBeforeFirst) {
      println(s"[DB]: no tasks found")
      None
    }
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
      for (task <- taskList)
        println(s"[DB]: task found: ${task.method} - $paramsstr - ${task.task_status} - " +
          s"${task.end_state} - ${task.task_result} - ${task.started_at} - ${task.finished_at} - ${task.time_spent}")
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
      for (task <- taskList)
        println(s"[DB]: task found: ${task.method} - $paramsstr - ${task.task_status} - " +
          s"${task.end_state} - ${task.task_result} - ${task.started_at} - ${task.finished_at} - ${task.time_spent}")
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
    for (_ <- 1 to methods.size)
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
    for (_ <- 1 to methods.size)
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