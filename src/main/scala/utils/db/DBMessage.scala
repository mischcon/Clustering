package utils.db

trait DBMessage

/*
 * REQUEST MESSAGES
 */

/* C */ case class CreateTask(method : String) extends DBMessage
/* R */ case class ReadTask(method : String) extends DBMessage
/* U */ case class UpdateTask(method : String, status : TaskStatus) extends DBMessage
/* D */ case class DeleteTask(method : String) extends DBMessage

/*
 * RESPONSE MESSAGES
 */

case class RequestedTask(method : String, status : TaskStatus, result : String) extends DBMessage
