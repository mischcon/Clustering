package vm.vagrant.configuration.builder

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import vm.vagrant.configuration.VagrantConfiguration
import vm.vagrant.configuration.VagrantEnvironmentConfig
import vm.vagrant.configuration.VagrantFileTemplateConfiguration
import vm.vagrant.configuration.VagrantFolderTemplateConfiguration
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantConfigurationBuilder {
  def create = new VagrantConfigurationBuilder
}

class VagrantConfigurationBuilder() {
  private var environmentConfig: VagrantEnvironmentConfig = _
  private var fileTemplateConfigurations = new List[VagrantFileTemplateConfiguration]
  private var folderTemplateConfigurations = new List[VagrantFolderTemplateConfiguration]

  def withVagrantEnvironmentConfig(environmentConfig: VagrantConfigurationBuilder): VagrantConfigurationBuilder = {
    this.environmentConfig = environmentConfig
    this
  }

  def withVagrantFileTemplateConfiguration(fileTemplateConfiguration: VagrantConfigurationBuilder): VagrantConfigurationBuilder = {
    fileTemplateConfigurations = fileTemplateConfigurations :+ fileTemplateConfiguration
    this
  }

  def withVagrantFolderTemplateConfiguration(folderTemplateConfiguration: VagrantConfigurationBuilder): VagrantConfigurationBuilder = {
    folderTemplateConfigurations = folderTemplateConfigurations :+ folderTemplateConfiguration
    this
  }

  def build: VagrantConfiguration = {
    if (environmentConfig == null) throw new VagrantBuilderException("No VagrantEnvironmentConfig defined")
    new VagrantConfiguration(environmentConfig, fileTemplateConfigurations, folderTemplateConfigurations)
  }
}
