package utils.messages

trait VmDirectoryServiceMessages

/* CONFIG */
case object GetVmConfig extends VmDirectoryServiceMessages
case class VmConfig(vmConfig : Object) extends VmDirectoryServiceMessages

/* STATUS */
case class Ready(vmConfig : Object) extends VmDirectoryServiceMessages