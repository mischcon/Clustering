package vm.messages

import java.io.File

/**
  * Created by oliver.ziegert on 19.04.17.
  */
trait NodeMonitorMessage

case object GetSystemAttributes extends NodeMonitorMessage

case class SetPath(path : File) extends NodeMonitorMessage

case class SetMaster(master: String) extends NodeMonitorMessage

case object StopMonitoring extends NodeMonitorMessage

case object StartMonitoring extends NodeMonitorMessage
