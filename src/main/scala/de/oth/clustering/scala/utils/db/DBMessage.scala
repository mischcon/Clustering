package de.oth.clustering.scala.utils.db

import java.sql.Timestamp


/**
  * = Message trait for [[de.oth.clustering.scala.utils.db.DBActor]] =
  */
trait DBMessage

/*
 * REQUEST MESSAGES
 */

/**
  * = Test connection to the database according to db.conf =
  */
case object ConnectionTest extends DBMessage

/**
  * = Request all table names from database =
  */
case object GetTables extends DBMessage

/**
  * = Request text report from database for ''DONE'' tasks =
  * <br>
  * Check ''src/main/resources/reports/'tableName'.txt'' for report.
  * @param tableName table name
  */
case class GenerateTextReport(tableName : String) extends DBMessage

/**
  * = Request json report from database for ''DONE'' tasks =
  * <br>
  * Check ''src/main/resources/reports/data.json'' for report.
  * @param tableName table name
  */
case class GenerateJsonReport(tableName : String) extends DBMessage

/**
  * = Request ''task_status - amount'' relation =
  * __Example__:
  * {{{
  * NOT_STARTED #
  * RUNNING     #
  * DONE        #
  * }}}
  * @param tableName table name
  */
case class CountTaskStatus(tableName : String) extends DBMessage

/**
  * = Request ''end_state - amount'' relation =
  * __Example__:
  * {{{
  * NONE      #
  * SUCCESS   #
  * FAILURE   #
  * ABANDONED #
  * ERROR     #
  * }}}
  * @param tableName table name
  */
case class CountEndState(tableName : String) extends DBMessage

/**
  * = Create task entry in the database =
  * @param method name of the task to be saved; __must be unique__
  * @param tableName table name
  */
case class CreateTask(method : String, tableName: String) extends DBMessage

/**
  * = Create task entry in the database =
  * @param method name of the task to be saved; __must be unique__
  * @param params parameters of the given task; if no parameters available use [[CreateTask]]
  * @param tableName table name
  */
case class CreateParametrizedTask(method : String, params : Map[String, String], tableName: String) extends DBMessage

/**
  * = Create several task entries in the database =
  * @param methods list w/ names of tasks to be saved; __names must be unique__
  * @param tableName table name
  */
case class CreateTasks(methods : List[String], tableName: String) extends DBMessage

/**
  * = Create several task entries in the database =
  * @param methods list w/ names and their parameters of tasks to be saved; __names must be unique__
  * @param tableName table name
  */
case class CreateParametrizedTasks(methods : Map[String, Map[String, String]], tableName: String) extends DBMessage

/**
  * = Get database entry for requested task =
  * expect Some([[de.oth.clustering.scala.utils.db.RequestedTask]]) or [[scala.None]] as responding message
  *
  * @param method name of requested task
  * @param tableName table name
  */
case class GetTask(method : String, tableName: String) extends DBMessage

/**
  * = Get database entries for requested tasks =
  * expect Some(List[ [[de.oth.clustering.scala.utils.db.RequestedTask]] ]) or [[scala.None]] as responding message
  *
  * @param methods list w/ names of requested tasks
  * @param tableName table name
  */
case class GetTasks(methods : List[String], tableName: String) extends DBMessage

/**
  * = Get database entries for tasks w/ requested status =
  * expect Some(List[ [[de.oth.clustering.scala.utils.db.RequestedTask]] ]) or [[scala.None]] as responding message
 *
  * @param task_status requested status from [[de.oth.clustering.scala.utils.db.TaskStatus]]
  * @param tableName   table name
  */
case class GetTasksWithStatus(task_status : TaskStatus, tableName: String) extends DBMessage

/**
  * = Update task in database =
 *
  * @param method      name of task to update
  * @param task_status new status from [[de.oth.clustering.scala.utils.db.TaskStatus]]
  * @param end_state   new end_state from [[de.oth.clustering.scala.utils.db.EndState]]
  * @param task_result new result
  * @param tableName   table name
  */
case class UpdateTask(method : String, task_status : TaskStatus, end_state : EndState, task_result : String,
                      tableName: String) extends DBMessage

/**
  * = Update several tasks in database =
 *
  * @param methods     list w/ names of tasks to update
  * @param task_status new status for all tasks from [[de.oth.clustering.scala.utils.db.TaskStatus]]
  * @param end_state   new end_state for all tasks from [[de.oth.clustering.scala.utils.db.EndState]]
  * @param task_result new result for all tasks
  * @param tableName   table name
  */
case class UpdateTasks(methods : List[String], task_status : TaskStatus, end_state : EndState, task_result : String,
                       tableName: String) extends DBMessage

/**
  * = Update task w/ new status in database =
 *
  * @param method      name of task to update
  * @param task_status new status from [[de.oth.clustering.scala.utils.db.TaskStatus]]
  * @param tableName   table name
  */
case class UpdateTaskStatus(method : String, task_status : TaskStatus, tableName: String) extends DBMessage

/**
  * = Update several tasks w/ new status in database =
 *
  * @param methods     list w/ names of tasks to update
  * @param task_status new status for all tasks from [[de.oth.clustering.scala.utils.db.TaskStatus]]
  * @param tableName   table name
  */
case class UpdateTasksStatus(methods : List[String], task_status : TaskStatus, tableName: String) extends DBMessage

/**
  * = Delete task from database =
  * @param method name of task to delete
  * @param tableName table name
  */
case class DeleteTask(method : String, tableName: String) extends DBMessage

/**
  * = Delete tasks from database =
  * @param methods list w/ names of tasks to delete
  * @param tableName table name
  */
case class DeleteTasks(methods : List[String], tableName: String) extends DBMessage

/*
 * RESPONSE MESSAGES
 */

/**
  * = Response message for [[de.oth.clustering.scala.utils.db.CountTaskStatus]] =
  *
  * @param result contains (task_status -> amount) key-value pairs
  */
case class CountedTaskStatus(result : Map[TaskStatus, Int]) extends DBMessage

/**
  * = Response message for [[de.oth.clustering.scala.utils.db.CountEndState]] =
  *
  * @param result contains (end_state -> amount) key-value pairs
  */
case class CountedEndState(result : Map[EndState, Int]) extends DBMessage

/**
  * = Response message for task entry =
  * @param method entry in tasks table for column '''method'''
  * @param params entry in tasks table for column '''params'''
  * @param task_status entry in tasks table for column '''task_status'''
  * @param end_state entry in tasks table for column '''end_state'''
  * @param task_result entry in tasks table for column '''task_result'''
  * @param started_at entry in tasks table for column '''started_at'''
  * @param finished_at entry in tasks table for column '''finished_at'''
  * @param time_spent entry in tasks table for column '''time_spent'''
  */
case class RequestedTask(method : String, params : Map[String, String], task_status : TaskStatus, end_state : EndState,
                         task_result : String, started_at : Timestamp, finished_at : Timestamp, time_spent : Int)
  extends DBMessage

/**
  * = Response message for [[de.oth.clustering.scala.utils.db.GetTables]] =
  *
  * @param names contains all table names in the cluster database
  */
case class Tables(names : List[String]) extends DBMessage

/**
  * = Response message for [[de.oth.clustering.scala.utils.db.GenerateTextReport]] or [[de.oth.de.oth.clustering.java.clustering.scala.utils.db.GenerateJsonReport]] =
  */
case class Report(path : String) extends DBMessage

/**
  * = Response message for [[de.oth.clustering.scala.utils.db.ConnectionTest]] =
  *
  * @param message true = OK; false = NOT OK
  */
case class ConnectionStatus(message : Boolean) extends DBMessage