package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */
/**
  * Global configuration for a Vagrant environment that uses {@link VagrantFileTemplateConfiguration} for a Vagrant environment
  *
  * @author oliver.ziegert
  *
  */
class VagrantConfiguration(environmentConfig: VagrantEnvironmentConfig, fileTemplateConfigurations: Iterable[VagrantFileTemplateConfiguration], folderTemplateConfigurations: Iterable[VagrantFolderTemplateConfiguration]) {

  /**
    * Returns the configuration of the Vagrant environment
    *
    * @return the configuration of the Vagrant environment
    */
  def getEnvironmentConfig: VagrantEnvironmentConfig = environmentConfig

  /**
    * Returns all {@link VagrantFileTemplateConfiguration} used by this configuration.
    *
    * @return all { @link VagrantFileTemplateConfiguration}
    */
  def getFileTemplateConfigurations: Iterable[VagrantFileTemplateConfiguration] = fileTemplateConfigurations

  def getFolderTemplateConfigurations: Iterable[VagrantFolderTemplateConfiguration] = folderTemplateConfigurations
}
