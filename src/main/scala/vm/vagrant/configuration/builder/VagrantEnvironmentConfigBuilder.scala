package vm.vagrant.configuration.builder

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import scala.collection.immutable.List
import vm.vagrant.configuration.VagrantEnvironmentConfig
import vm.vagrant.configuration.VagrantVmConfig
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantEnvironmentConfigBuilder {
  def create = new VagrantEnvironmentConfigBuilder
}

class VagrantEnvironmentConfigBuilder() {
  private var vmConfigs = new List[VagrantVmConfig]

  def withVagrantVmConfig(vmConfig: VagrantVmConfig): VagrantEnvironmentConfigBuilder = {
    vmConfigs = vmConfigs :+ vmConfig
    this
  }

  def build: VagrantEnvironmentConfig = {
    if (vmConfigs.isEmpty) throw new VagrantBuilderException("No vm defined")
    new VagrantEnvironmentConfig(vmConfigs)
  }
}