package vm.vagrant.configuration

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
class VagrantProvisionerConfig(debug: Boolean, manifestsPath: String, manifestFile: String, modulesPath: String) {

/**
  * Creates a new {@link PuppetProvisionerConfig}
  */
  /**
    * Returns the name of the Puppet manifest
    *
    * @return the name of the Puppet manifest
    */
  def getManifestFile: String = manifestFile

  /**
    * Returns the path of the Puppet manifest
    *
    * @return the path of the Puppet manifest
    */
  def getManifestsPath: String = manifestsPath

  /**
    * Returns the path to all Puppet modules
    *
    * @return the path to all Puppet modules
    */
  def getModulesPath: String = modulesPath

  /**
    * Returns true if debugging for Puppet is activated. This will log more
    * information from puppet on the console when creating a VM
    *
    * @return true if debugging for Puppet is activated
    */
  def isDebug: Boolean = debug
}

