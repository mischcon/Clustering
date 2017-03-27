package utils.db

/**
  * = Message trait for [[utils.db.DBActor]] =
  */
trait DBMessage

/*
 * REQUEST MESSAGES
 */

/**
  * = Create task entry in the database =
  * @param method name of the task to be saved; __must be unique__
  */
case class CreateTask(method : String) extends DBMessage

/**
  * = Create several task entries in the database =
  * @param methods list w/ names of tasks to be saved; __names must be unique__
  */
case class CreateTasks(methods : List[String]) extends DBMessage

/**
  * = Get database entry for requested task =
  * expect Some([[utils.db.RequestedTask]]) or [[scala.None]] as responding message
  * @param method name of requested task
  */
case class GetTask(method : String) extends DBMessage

/**
  * = Get database entries for requested tasks =
  * expect Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]] as responding message
  * @param methods list w/ names of requested tasks
  */
case class GetTasks(methods : List[String]) extends DBMessage

/**
  * = Get database entries for tasks w/ requested status =
  * expect Some(List[ [[utils.db.RequestedTask]] ]) or [[scala.None]] as responding message
  * @param status requested status from [[utils.db.TaskStatus]]
  */
case class GetTasksWithStatus(status : TaskStatus) extends DBMessage

/**
  * = Update task w/ new status in database =
  * @param method name of requested task
  * @param status new status from [[utils.db.TaskStatus]]
  */
case class UpdateTaskStatus(method : String, status : TaskStatus) extends DBMessage

/**
  * = Update several tasks w/ new status in database =
  * @param methods list w/ names of requested tasks
  * @param status new status for all tasks from [[utils.db.TaskStatus]]
  */
case class UpdateTasksStatus(methods : List[String], status : TaskStatus) extends DBMessage

/**
  * = Delete requested task from database =
  * @param method name of requested task
  */
case class DeleteTask(method : String) extends DBMessage

/**
  * = Delete requested tasks from database =
  * @param methods list w/ names of requested tasks
  */
case class DeleteTasks(methods : List[String]) extends DBMessage

/*
 * RESPONSE MESSAGES
 */

/**
  * = Response message class of [[utils.db.DBActor]] =
  * @param method entry in tasks table for column '''method'''
  * @param status entry in tasks table for column '''status'''
  * @param result entry in tasks table for column '''result'''
  */
case class RequestedTask(method : String, status : TaskStatus, result : String) extends DBMessage