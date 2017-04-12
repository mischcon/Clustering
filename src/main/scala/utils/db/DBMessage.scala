package utils.db

import java.sql.Timestamp


/**
  * = Message trait for [[utils.db.DBActor]] =
  */
trait DBMessage

/*
 * REQUEST MESSAGES
 */

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
case class CountTaskStatus(tableName: String) extends DBMessage

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
case class CountEndState(tableName: String) extends DBMessage

/**
  * = Create task entry in the database =
  * @param method name of the task to be saved; __must be unique__
  * @param tableName table name
  */
case class CreateTask(method : String, tableName: String) extends DBMessage

/**
  * = Create several task entries in the database =
  * @param methods list w/ names of tasks to be saved; __names must be unique__
  * @param tableName table name
  */
case class CreateTasks(methods : List[String], tableName: String) extends DBMessage

/**
  * = Get database entry for requested task =
  * expect Some([[utils.db.RequestedTask]]) or [[scala.None]] as responding message
  * @param method name of requested task
  * @param tableName table name
  */
case class GetTask(method : String, tableName: String) extends DBMessage

/**
  * = Get database entries for requested tasks =
  * expect Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]] as responding message
  * @param methods list w/ names of requested tasks
  * @param tableName table name
  */
case class GetTasks(methods : List[String], tableName: String) extends DBMessage

/**
  * = Get database entries for tasks w/ requested status =
  * expect Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]] as responding message
  * @param task_status requested status from [[utils.db.TaskStatus]]
  * @param tableName table name
  */
case class GetTasksWithStatus(task_status : TaskStatus, tableName: String) extends DBMessage

/**
  * = Update task in database =
  * @param method name of task to update
  * @param task_status new status from [[utils.db.TaskStatus]]
  * @param end_state new end_state from [[utils.db.EndState]]
  * @param task_result new result
  * @param tableName table name
  */
case class UpdateTask(method : String, task_status : TaskStatus, end_state : EndState, task_result : String,
                      tableName: String) extends DBMessage

/**
  * = Update several tasks in database =
  * @param methods list w/ names of tasks to update
  * @param task_status new status for all tasks from [[utils.db.TaskStatus]]
  * @param end_state new end_state for all tasks from [[utils.db.EndState]]
  * @param task_result new result for all tasks
  * @param tableName table name
  */
case class UpdateTasks(methods : List[String], task_status : TaskStatus, end_state : EndState, task_result : String,
                       tableName: String) extends DBMessage

/**
  * = Update task w/ new status in database =
  * @param method name of task to update
  * @param task_status new status from [[utils.db.TaskStatus]]
  * @param tableName table name
  */
case class UpdateTaskStatus(method : String, task_status : TaskStatus, tableName: String) extends DBMessage

/**
  * = Update several tasks w/ new status in database =
  * @param methods list w/ names of tasks to update
  * @param task_status new status for all tasks from [[utils.db.TaskStatus]]
  * @param tableName table name
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
  * = Response message for [[utils.db.CountTaskStatus]] =
  * @param result contains (task_status -> amount) key-value pairs
  */
case class CountedTaskStatus(result : Map[TaskStatus, Int]) extends DBMessage

/**
  * = Response message for [[utils.db.CountEndState]] =
  * @param result contains (end_state -> amount) key-value pairs
  */
case class CountedEndState(result : Map[EndState, Int]) extends DBMessage

/**
  * = Response message for task entry =
  * @param method entry in tasks table for column '''method'''
  * @param task_status entry in tasks table for column '''task_status'''
  * @param end_state entry in tasks table for column '''end_state'''
  * @param task_result entry in tasks table for column '''task_result'''
  * @param started_at entry in tasks table for column '''started_at'''
  * @param finished_at entry in tasks table for column '''finished_at'''
  * @param time_spent entry in tasks table for column '''time_spent'''
  */
case class RequestedTask(method : String, task_status : TaskStatus, end_state : EndState, task_result : String,
                         started_at : Timestamp, finished_at : Timestamp, time_spent : Int) extends DBMessage