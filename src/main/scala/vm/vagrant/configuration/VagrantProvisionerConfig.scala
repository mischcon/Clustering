package vm.vagrant.configuration

import java.io.File

import vm.vagrant.configuration.Run.Run

/**
  * Created by oliver.ziegert on 24.03.17.
  */
/**
  * Configuration for a puppet provisioner that is used by Vagrant to configure a
  * VM
  *
  * @author oliver.ziegert
  *
  */

trait VagrantProvisionerConfig {
  def mode: String
  def run: Run
  def preserveOrder: Boolean
}

object Run extends Enumeration {
  type Run = Value
  val always, never, once = Value
}

class VagrantProvisionerFileConfig(var _run: Run,
                                   var _preserveOrder: Boolean,
                                   var source: File,
                                   var destination: File) extends VagrantProvisionerConfig {
  override def mode: String = "file"
  override def run: Run = _run
  override def preserveOrder: Boolean = _preserveOrder
}

class VagrantProvisionerShellConfig(var _run: Run,
                                    var _preserveOrder: Boolean,
                                    var inline: String,
                                    var path: File,
                                    var args: List[String],
                                    var binary: Boolean,
                                    var privileged: Boolean,
                                    var uploadPath: File,
                                    var keepColor: Boolean,
                                    var name: String,
                                    var md5: String,
                                    var sha1: String) extends VagrantProvisionerConfig {
  override def mode: String = "shell"
  override def run: Run = _run
  override def preserveOrder: Boolean = _preserveOrder
}
