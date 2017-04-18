package vm.vagrant.configuration

import java.io.File

/**
  * Created by oliver.ziegert on 24.03.17.
  */

/**
  * Holds the configuration of a Vagrant environmant.
  *
  * @author oliver.ziegert
  *
  */
class VagrantEnvironmentConfig(var vmConfigs: Iterable[VagrantVmConfig],
                               var path: File,
                               var version: String) {

  /**
    * Returns true if this configuration describes a multi VM environment. A multi VM environment manages more than one VM.
    *
    * @return true if this configuration describes a multi VM environment
    */
  def isMultiVmEnvironment: Boolean = {
    if (vmConfigs.size > 1) return true
    false
  }
}
