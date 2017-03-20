package vm.messages

/**
  * Created by mischcon on 3/20/17.
  */

trait VMRequestMessage

case class VMRequest(contextpath : String, method : String, body : String,
                     headers : Map[String, String], params : Map[String, String]) extends VMRequestMessage
