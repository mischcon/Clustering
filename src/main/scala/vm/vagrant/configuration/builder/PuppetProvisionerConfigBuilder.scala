package vm.vagrant.configuration.builder

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import vm.vagrant.configuration.VagrantProvisionerConfig
import vm.vagrant.configuration.builder.util.VagrantBuilderException


/**
  * Builder for {@link PuppetProvisionerConfig}
  *
  * @author oliver.ziegert
  *
  */
object PuppetProvisionerConfigBuilder {
  /**
    * creates a new {@link PuppetProvisionerConfigBuilder}
    *
    * @return a new { @link PuppetProvisionerConfigBuilder}
    */
  def create = new PuppetProvisionerConfigBuilder
}

class PuppetProvisionerConfigBuilder() {
  private var manifestPath: String = _
  private var manifestFile: String = _
  private var debug: Boolean = _
  private var modulesPath: String = _

  def withManifestFile(manifestFile: String): PuppetProvisionerConfigBuilder = {
    this.manifestFile = manifestFile
    this
  }

  def withManifestPath(manifestPath: String): PuppetProvisionerConfigBuilder = {
    this.manifestPath = manifestPath
    this
  }

  def withModulesPath(modulesPath: String): PuppetProvisionerConfigBuilder = {
    this.modulesPath = modulesPath
    this
  }

  def withDebug(debug: Boolean): PuppetProvisionerConfigBuilder = {
    this.debug = debug
    this
  }

  def build: VagrantProvisionerConfig = {
    if (manifestPath == null) throw new VagrantBuilderException("no manifestPath defined!")
    if (manifestFile == null) throw new VagrantBuilderException("no manifestFile defined!")
    new VagrantProvisionerConfig(debug, manifestPath, manifestFile, modulesPath)
  }
}
