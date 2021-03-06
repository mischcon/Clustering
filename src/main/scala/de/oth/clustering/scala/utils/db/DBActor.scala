package de.oth.clustering.scala.utils.db

import java.sql.{Connection, DatabaseMetaData, DriverManager, ResultSet}

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.{Config, ConfigFactory}

/**
  * = Actor-based interface between cluster actors & database =
  *{{{
  * Current database scheme (de.oth.clustering.java.clustering):
  *
  * 0 .. * tasks_[...]:
  * +-----+--------+--------+-------------+-----------+-------------+------------+-------------+------------+
  * | id  | method | params | task_status | end_state | task_result | started_at | finished_at | time_spent |
  * +-----+--------+--------+-------------+-----------+-------------+------------+-------------+------------+
  * | int | string | string | NOT_STARTED | SUCCESS   | string      | timestamp  | timestamp   | int        |
  * |     |        |        | RUNNING     | FAILURE   |             |            |             |            |
  * |     |        |        | DONE        | ABANDONED |             |            |             |            |
  * |     |        |        |             | ERROR     |             |            |             |            |
  * |     |        |        |             | null      |             |            |             |            |
  * +-----+--------+--------+-------------+-----------+-------------+------------+-------------+------------+
  * }}}
  * All messages that are meant to be sent to this actor are of type [[de.oth.clustering.scala.utils.db.DBMessage]].
  *
  * @param config config file or config from src/main/resources by default
  * {{{
  *
  * }}}
  */
class DBActor(config : Config = ConfigFactory.load("db.conf"))
  extends Actor with ActorLogging {

  /**
    * = Connects to the configured database =
    * Configuration is found @ ''application.conf'' @ section ''db''
    * @return [[java.sql.Connection]] Object or [[scala.None]]
    * @todo adjust after outsourcing the db.conf
    */
  def connect: Option[Connection] = {
    try {
      Class.forName(config.getString("db.driver"))
      Some(DriverManager.getConnection(
        config.getString("db.url"),
        config.getString("db.username"),
        config.getString("db.password")))
    }
    catch {
      case e: Exception =>
        log.error(e, "check db.conf")
        None
    }
  }

  /**
    * = Checks if table w/ __tableName__ exists =
    * @param connection connection object
    * @param tableName table name
    * @return true if table exists
    */
  def checkTableExistence(connection: Connection, tableName : String): Boolean = {
    val metadata : DatabaseMetaData = connection.getMetaData
    val resultSet : ResultSet = metadata.getTables(null, null, "%", null)
    var tableExists : Boolean = false
    while (resultSet.next()) {
      if (resultSet.getString(3).equals(tableName))
        tableExists = true
    }
    tableExists
  }

  /**
    * = Performs database query =
    *
    * @param query requested query of type [[de.oth.clustering.scala.utils.db.DBQuery]]
    */
  def performQuery(query : DBQuery): Unit = {
    connect match {
      case Some(connection) =>
        if (!checkTableExistence(connection, query.table)) {
          /*
              the only query w/o tableName parameter
           */
          if (query.getClass != classOf[DBGetTables]) {
            new DBCreateTasksTable(query.table).perform(connection)
            log.debug(s"table '${query.table} created.'")
          }
        }
        query.perform(connection) match {
          case ()  =>
          case msg => sender() ! msg
        }
        connection.close()
      case None =>
        log.info("could not connect")
    }
  }

  /**
    * = Answers w/ [[de.oth.clustering.scala.utils.db.Tables]] =
    */
  //noinspection AccessorLikeMethodIsUnit
  def getTables(): Unit = {
    log.debug("performing 'DBGetTables'")
    performQuery(new DBGetTables)
  }

  /**
    * = Generates text report for all tasks w/ status ''DONE'' =
    * @param tableName table name
    */
  def generateTextReport(tableName : String): Unit = {
    log.debug(s"performing 'DBGenerateTextReport' for '$tableName'")
    performQuery(new DBGenerateTextReport(tableName))
  }

  /**
    * = Generates json report for all tasks w/ status ''DONE'' =
    * @param tableName table name
    */
  def generateJsonReport(tableName : String): Unit = {
    log.debug(s"performing 'DBGenerateJsonReport' for '$tableName'")
    performQuery(new DBGenerateJsonReport(tableName))
  }

  /**
    * = Answers w/ [[de.oth.clustering.scala.utils.db.CountedTaskStatus]] =
    *
    * @param tableName table name
    */
  def countTaskStatus(tableName : String): Unit = {
    log.debug(s"performing 'DBCountTaskStatus' for '$tableName'")
    performQuery(new DBCountTaskStatus(tableName))
  }

  /**
    * = Answers w/ [[de.oth.clustering.scala.utils.db.CountedEndState]] =
    *
    * @param tableName table name
    */
  def countEndState(tableName : String): Unit = {
    log.debug(s"performing 'DBCountEndState' for '$tableName'")
    performQuery(new DBCountEndState(tableName))
  }

  /**
    * = Creates task entry in the database =
    * @param method name of the task to be saved; __must be unique__
    * @param tableName table name
    */
  def createTask(method : String, tableName: String): Unit = {
    log.debug(s"performing 'DBCreateTask' for '$tableName'")
    performQuery(new DBCreateTask(method, tableName))
  }

  /**
    * = Creates task entry in the database =
    * @param method name of the task to be saved; __must be unique__
    * @param params parameters of the given task
    * @param tableName table name
    */
  def createTask(method : String, params : Map[String, String], tableName: String): Unit = {
    log.debug(s"performing 'DBCreateTask' for '$tableName'")
    performQuery(new DBCreateTask(method, params, tableName))
  }

  /**
    * = Creates several task entries in the database =
    * @param methods list w/ names of tasks to be saved; __names must be unique__
    * @param tableName table name
    */
  def createTasks(methods : List[String], tableName: String): Unit = {
    log.debug(s"performing 'DBCreateTasks' for '$tableName'")
    performQuery(new DBCreateTasks(methods, tableName))
  }

  /**
    * = Creates several task entries in the database =
    * @param methods list w/ names and their parameters of tasks to be saved; __names must be unique__
    * @param tableName table name
    */
  def createTasks(methods : Map[String, Map[String, String]], tableName: String): Unit = {
    log.debug(s"performing 'DBCreateTasks' for '$tableName'")
    performQuery(new DBCreateTasks(methods, tableName))
  }

  /**
    * = Answers w/ requested task =
    * Some([[de.oth.clustering.scala.utils.db.RequestedTask]]) or [[scala.None]]
    *
    * @param method name of requested task
    * @param tableName table name
    */
  //noinspection AccessorLikeMethodIsUnit
  def getTask(method : String, tableName: String): Unit = {
    log.debug(s"performing 'DBGetTask' for '$tableName'")
    performQuery(new DBGetTask(method, tableName))
  }

  /**
    * = Answers w/ requested tasks =
    * Some(List[ [[de.oth.clustering.scala.utils.db.RequestedTask]] ]) or [[scala.None]]
    *
    * @param methods list w/ names of requested tasks
    * @param tableName table name
    */
  //noinspection AccessorLikeMethodIsUnit
  def getTasks(methods : List[String], tableName: String): Unit = {
    log.debug(s"performing 'DBGetTasks' for '$tableName'")
    performQuery(new DBGetTasks(methods, tableName))
  }

  /**
    * = Answers w/ requested tasks =
    * Some(List[ [[de.oth.clustering.scala.utils.db.RequestedTask]] ]) or [[scala.None]]
    *
    * @param task_status requested status from [[de.oth.clustering.scala.utils.db.TaskStatus]]
    * @param tableName   table name
    */
  //noinspection AccessorLikeMethodIsUnit
  def getTasksWithStatus(task_status: TaskStatus, tableName: String): Unit = {
    log.debug(s"performing 'DBGetTasksWithStatus' for '$tableName'")
    performQuery(new DBGetTasksWithStatus(task_status, tableName))
  }

  /**
    * = Updates task w/ new values =
 *
    * @param method      name of task to update
    * @param task_status new status from [[de.oth.clustering.scala.utils.db.TaskStatus]]
    * @param end_state   new end_state from [[de.oth.clustering.scala.utils.db.EndState]]
    * @param task_result new result
    * @param tableName   table name
    */
  def updateTask(method : String, task_status : TaskStatus, end_state : EndState, task_result : String,
                 tableName: String): Unit = {
    log.debug(s"performing 'DBUpdateTask' for '$tableName'")
    performQuery(new DBUpdateTask(method, task_status, end_state, task_result, tableName))
  }

  /**
    * = Updates several tasks w/ new values =
 *
    * @param methods     list w/ names of tasks to update
    * @param task_status new status for all tasks from [[de.oth.clustering.scala.utils.db.TaskStatus]]
    * @param end_state   new end_state for all tasks from [[de.oth.clustering.scala.utils.db.EndState]]
    * @param task_result new result for all tasks
    * @param tableName   table name
    */
  def updateTasks(methods : List[String], task_status : TaskStatus, end_state : EndState, task_result : String,
                  tableName: String): Unit = {
    log.debug(s"performing 'DBUpdateTasks' for '$tableName'")
    performQuery(new DBUpdateTasks(methods, task_status, end_state, task_result, tableName))
  }

  /**
    * = Updates task w/ new status =
 *
    * @param method      name of task to update
    * @param task_status new status from [[de.oth.clustering.scala.utils.db.TaskStatus]]
    * @param tableName   table name
    */
  def updateTaskStatus(method : String, task_status : TaskStatus, tableName: String): Unit = {
    log.debug(s"performing 'DBUpdateTaskStatus' for '$tableName'")
    performQuery(new DBUpdateTaskStatus(method, task_status, tableName))
  }

  /**
    * = Updates several tasks w/ new status =
 *
    * @param methods     list w/ names of tasks to update
    * @param task_status new status for all tasks from [[de.oth.clustering.scala.utils.db.TaskStatus]]
    * @param tableName   table name
    */
  def updateTasksStatus(methods : List[String], task_status : TaskStatus, tableName: String): Unit = {
    log.debug(s"performing 'DBUpdateTasksStatus' for '$tableName'")
    performQuery(new DBUpdateTasksStatus(methods, task_status, tableName))
  }

  /**
    * = Deletes requested task =
    * @param method name of task to delete
    * @param tableName table name
    */
  def deleteTask(method : String, tableName: String): Unit = {
    log.debug(s"performing 'DBDeleteTask' for '$tableName'")
    performQuery(new DBDeleteTask(method, tableName))
  }

  /**
    * = Deletes requested tasks =
    * @param methods list w/ names of tasks to delete
    * @param tableName table name
    */
  def deleteTasks(methods : List[String], tableName: String): Unit = {
    log.debug(s"performing 'DBDeleteTasks' for '$tableName'")
    performQuery(new DBDeleteTasks(methods, tableName))
  }

  override def receive: Receive = {
    case ConnectionTest =>
      connect match {
        case Some(connection) =>
          val url = config.getString("db.url")
          connection.close()
          log.info(s"DB connection successful. Connected to ${url.split("\\?")(0)}")
          sender() ! ConnectionStatus(true)
        case None =>
          log.info("could not connect")
          sender() ! ConnectionStatus(false)
      }
    case GetTables =>
      getTables()
    case GenerateTextReport(tableName) =>
      generateTextReport(tableName)
    case GenerateJsonReport(tableName) =>
      generateJsonReport(tableName)
    case CountTaskStatus(tableName) =>
      countTaskStatus(tableName)
    case CountEndState(tableName) =>
      countEndState(tableName)
    case CreateTask(method, tableName) =>
      createTask(method, tableName)
    case CreateParametrizedTask(method, params, tableName) =>
      createTask(method, params, tableName)
    case CreateTasks(methods, tableName) =>
      createTasks(methods, tableName)
    case CreateParametrizedTasks(methods, tableName) =>
      createTasks(methods, tableName)
    case GetTask(method, tableName) =>
      getTask(method, tableName)
    case GetTasks(methods, tableName) =>
      getTasks(methods, tableName)
    case GetTasksWithStatus(task_status, tableName) =>
      getTasksWithStatus(task_status, tableName)
    case UpdateTask(method, task_status, end_state, task_result, tableName) =>
      updateTask(method, task_status, end_state, task_result, tableName)
    case UpdateTasks(methods, task_status, end_state, task_result, tableName) =>
      updateTasks(methods, task_status, end_state, task_result, tableName)
    case UpdateTaskStatus(method, task_status, tableName) =>
      updateTaskStatus(method, task_status, tableName)
    case UpdateTasksStatus(methods, task_status, tableName) =>
      updateTasksStatus(methods, task_status, tableName)
    case DeleteTask(method, tableName) =>
      deleteTask(method, tableName)
    case DeleteTasks(methods, tableName) =>
      deleteTasks(methods, tableName)
    case msg =>
      log.info(s"unknown message type : $msg")
  }
}
