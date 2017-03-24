package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

/**
  * Holds the configuration of a Vagrant environmant.
  *
  * @author oliver.ziegert
  *
  */
class VagrantEnvironmentConfig(vmConfigs: Iterable[VagrantVmConfig]) {
  /**
    * Returns all {@link VagrantVmConfig}
    *
    * @return all { @link VagrantVmConfig}
    */
  def getVmConfigs: Iterable[VagrantVmConfig] = vmConfigs

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
