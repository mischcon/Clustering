package utils.db

trait DBMessage

/*
 * REQUEST MESSAGES
 */

case class CreateTask(method : String) extends DBMessage
case class CreateTasks(methods : List[String]) extends DBMessage
case class GetTask(method : String) extends DBMessage
case class GetTasksWithStatus(status : TaskStatus) extends DBMessage
case class UpdateTaskStatus(method : String, status : TaskStatus) extends DBMessage
case class DeleteTask(method : String) extends DBMessage

/*
 * RESPONSE MESSAGES
 */

case class RequestedTask(method : String, status : TaskStatus, result : String) extends DBMessage