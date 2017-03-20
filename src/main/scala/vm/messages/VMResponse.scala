package vm.messages

/**
  * Created by mischcon on 3/20/17.
  */

trait VMResponseMessage

case class VMResponse(body : String, status_code : Integer, headers : Map[String, String]) extends VMResponseMessage
