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
class VagrantProviderConfig(name : String, guiMode: Boolean, memory: Int, cpus: Int, customize: Iterable[Array[String]]) {

  def getName: String = name

  /**
    * Returns true if gui mode is active for the VM. This means that VirtualBox is not running in headless mode.
    *
    * @return true if gui mode is active
    */
  def isGuiMode: Boolean = guiMode

  def getMemory: Int = memory

  def getCpus: Int = cpus

  def getCustomize: Iterable[Array[String]] = customize

}

