package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import vm.vagrant.configuration.PuppetProvisionerConfig
import vm.vagrant.configuration.VagrantPortForwarding
import java.net.URL
import scala.collection.immutable.List

/**
  * A configuration class that can be used to define and create a VM in Vagrant.
  *
  * @author oliver.ziegert
  *
  */
class VagrantVmConfig(name: String, ip: String, hostName: String, boxName: String, boxUrl: URL, portForwardings: Iterable[VagrantPortForwarding], puppetProvisionerConfig: PuppetProvisionerConfig, guiMode: Boolean) {

  /**
    * Returns the host name of the VM
    *
    * @return the host name of the VM
    */
  def getHostName: String = hostName

  /**
    * Returns true if gui mode is active for the VM. This means that VirtualBox is not running in headless mode.
    *
    * @return true if gui mode is active
    */
  def isGuiMode: Boolean = guiMode

  /**
    * Returns the name of the box Vagrant will use as template for the VM
    *
    * @return the name of the box
    */
  def getBoxName: String = boxName

  /**
    * Returns the URL of the box Vagrant will use as template for the VM. If the box with the given name is not installed on your system Vagrant will download it by using this URL.
    *
    * @return the URL of the box
    */
  def getBoxUrl: URL = boxUrl

  /**
    * Returns the puppet configuration for the VM
    *
    * @return the puppet configuration
    */
  def getPuppetProvisionerConfig: PuppetProvisionerConfig = puppetProvisionerConfig

  /**
    * Returns a iterator for all port forwardings of the VM
    *
    * @return a iterator for all port forwardings
    */
  def getPortForwardings: Iterable[VagrantPortForwarding] = portForwardings

  /**
    * Returns the name of the VM
    *
    * @return the name of the VM
    */
  def getName: String = name

  /**
    * Returns the static ip for the VM
    *
    * @return the static ip for the VM
    */
  def getIp: String = ip
}
