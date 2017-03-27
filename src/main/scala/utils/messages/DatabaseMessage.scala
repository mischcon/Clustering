package utils.messages

/**
  * Created by mischcon on 3/20/17.
  */

/* REQUEST */

@Deprecated("leave for documentation")
trait DataBaseMessageRequest

case class CreateTaskEntry(task : String, vmId : String, status : String) extends DataBaseMessageRequest
case class UpdateTaskEntry(task : String, vmId: String, status : String) extends DataBaseMessageRequest
case class RequestTaskEntry(task : String) extends DataBaseMessageRequest
case class DeleteTaskEntry(task : String) extends DataBaseMessageRequest

case class CreateRatioEntry(vmId : String, numberOfTasks : Integer = 0) extends DataBaseMessageRequest
case class DeleteRatioEntry(vmId : String) extends DataBaseMessageRequest
case object RequestSmallestVM extends DataBaseMessageRequest
case class DecreaseTaskCounter(vmId: String) extends DataBaseMessageRequest

/* RESPONSE */

@Deprecated("leave for documentation")
trait DataBaseMessageResponse

case class ResponseTaskEntry(task : String, vmId : String, status : String) extends DataBaseMessageRequest
case class ResponseSmallestVM(vmId : String) extends DataBaseMessageResponse