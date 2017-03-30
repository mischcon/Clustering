package utils.db

import java.sql.{Connection, DriverManager}

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
  * All messages that are meant to be sent to this actor are of type [[utils.db.DBMessage]].
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
      println("[DBActor]: " + e.getMessage)
      None
  }

  /**
    * = Performs database query =
    * @param query requested query of type [[utils.db.DBQuery]]
    */
  def performQuery(query : DBQuery): Unit = {
    connect match {
      case Some(connection) =>
        try {
          query.perform(connection) match {
            case ()  =>
            case msg => sender() ! msg
          }
        }
        catch {
          case e: Exception =>
            println("[DBActor]: " + e.getMessage)
        }
        finally connection.close()
      case None =>
        println("[DBActor]: could not connect")
    }
  }

  /**
    * = Answers w/ [[utils.db.CountedTaskStatus]] =
    */
  def countTaskStatus(): Unit = {
    performQuery(new DBCountTaskStatus)
  }

  /**
    * = Answers w/ [[utils.db.CountedEndState]] =
    */
  def countEndState(): Unit = {
    performQuery(new DBCountEndState)
  }

  /**
    * = Creates task entry in the database =
    * @param method name of the task to be saved; __must be unique__
    */
  def createTask(method : String): Unit = {
    performQuery(new DBCreateTask(method))
  }

  /**
    * = Creates several task entries in the database =
    * @param methods list w/ names of tasks to be saved; __names must be unique__
    */
  def createTasks(methods : List[String]): Unit = {
    performQuery(new DBCreateTasks(methods))
  }

  /**
    * = Answers w/ requested task =
    * Some([[utils.db.RequestedTask]]) or [[scala.None]]
    * @param method name of requested task
    */
  def getTask(method : String): Unit = {
    performQuery(new DBGetTask(method))
  }

  /**
    * = Answers w/ requested tasks =
    * Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]]
    * @param methods list w/ names of requested tasks
    */
  def getTasks(methods : List[String]): Unit = {
    performQuery(new DBGetTasks(methods))
  }

  /**
    * = Answers w/ requested tasks =
    * Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]]
    * @param task_status requested status from [[utils.db.TaskStatus]]
    */
  def getTasksWithStatus(task_status: TaskStatus): Unit = {
    performQuery(new DBGetTasksWithStatus(task_status))
  }

  /**
    * = Updates task w/ new values =
    * @param method name of task to update
    * @param task_status new status from [[utils.db.TaskStatus]]
    * @param end_state new end_state from [[utils.db.EndState]]
    * @param task_result new result
    */
  def updateTask(method : String, task_status : TaskStatus,
                 end_state : EndState, task_result : String): Unit = {
    performQuery(new DBUpdateTask(method, task_status, end_state, task_result))
  }

  /**
    * = Updates several tasks w/ new values =
    * @param methods list w/ names of tasks to update
    * @param task_status new status for all tasks from [[utils.db.TaskStatus]]
    * @param end_state new end_state for all tasks from [[utils.db.EndState]]
    * @param task_result new result for all tasks
    */
  def updateTasks(methods : List[String], task_status : TaskStatus,
                  end_state : EndState, task_result : String): Unit = {
    performQuery(new DBUpdateTasks(methods, task_status, end_state, task_result))
  }

  /**
    * = Updates task w/ new status =
    * @param method name of task to update
    * @param task_status new status from [[utils.db.TaskStatus]]
    */
  def updateTaskStatus(method : String, task_status : TaskStatus): Unit = {
    performQuery(new DBUpdateTaskStatus(method, task_status))
  }

  /**
    * = Updates several tasks w/ new status =
    * @param methods list w/ names of tasks to update
    * @param task_status new status for all tasks from [[utils.db.TaskStatus]]
    */
  def updateTasksStatus(methods : List[String], task_status : TaskStatus): Unit = {
    performQuery(new DBUpdateTasksStatus(methods, task_status))
  }

  /**
    * = Deletes requested task =
    * @param method name of task to delete
    */
  def deleteTask(method : String): Unit = {
    performQuery(new DBDeleteTask(method))
  }

  /**
    * = Deletes requested tasks =
    * @param methods list w/ names of tasks to delete
    */
  def deleteTasks(methods : List[String]): Unit = {
    performQuery(new DBDeleteTasks(methods))
  }

  override def receive: Receive = {
    case CountTaskStatus =>
      countTaskStatus()
    case CountEndState =>
      countEndState()
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
    case msg =>
      println(s"[DBActor]: I do not process messages of type $msg")
  }
}
