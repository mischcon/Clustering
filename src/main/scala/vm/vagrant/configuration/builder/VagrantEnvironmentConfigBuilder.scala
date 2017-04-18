package vm.vagrant.configuration.builder

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File

import scala.collection.immutable.List
import vm.vagrant.configuration.VagrantEnvironmentConfig
import vm.vagrant.configuration.VagrantVmConfig
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantEnvironmentConfigBuilder {
  def create = new VagrantEnvironmentConfigBuilder
}

class VagrantEnvironmentConfigBuilder() {
  private var vmConfigs: List[VagrantVmConfig] = List()
  private var path: File = _
  private var version: String = _

  def withVagrantVmConfig(vmConfig: VagrantVmConfig): VagrantEnvironmentConfigBuilder = {
    vmConfigs ::= vmConfig
    this
  }

  def withPath(path: File): VagrantEnvironmentConfigBuilder = {
    this.path = path
    this
  }

  def withVersion(version: String): VagrantEnvironmentConfigBuilder = {
    this.version = version
    this
  }

  def build: VagrantEnvironmentConfig = {
    if (vmConfigs.isEmpty) throw new VagrantBuilderException("No vm defined")
    if (path == null) throw new VagrantBuilderException("No path defined")
    if (version == null || version.isEmpty) new VagrantBuilderException("No version defined")
    new VagrantEnvironmentConfig(vmConfigs = vmConfigs,
                                 path = path,
                                 version = version)
  }
}