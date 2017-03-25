package vm.vagrant.configuration.builder

import scala.collection.immutable.List
import java.net.MalformedURLException
import java.net.URL
import vm.vagrant.configuration.PuppetProvisionerConfig
import vm.vagrant.configuration.VagrantPortForwarding
import vm.vagrant.configuration.VagrantVmConfig
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantVmConfigBuilder {
  def create = new VagrantVmConfigBuilder
}

class VagrantVmConfigBuilder() {
  private var portForwardings = List[VagrantPortForwarding]()
  private var puppetProvisionerConfig: PuppetProvisionerConfig = _
  private var name: String = _
  private var ip: String = _
  private var boxName: String = _
  private var boxUrl: URL = _
  private var hostName: String = _
  private var guiMode: Boolean = _


  def withPuppetProvisionerConfig(puppetProvisionerConfig: PuppetProvisionerConfig): VagrantVmConfigBuilder = {
    this.puppetProvisionerConfig = puppetProvisionerConfig
    this
  }

  def withVagrantPortForwarding(portForwarding: VagrantPortForwarding): VagrantVmConfigBuilder = {
    portForwardings = portForwardings :+ portForwarding
    this
  }

  def withHostName(hostName: String): VagrantVmConfigBuilder = {
    this.hostName = hostName
    this
  }

  def withGuiMode(guiMode: Boolean): VagrantVmConfigBuilder = {
    this.guiMode = guiMode
    this
  }

  def withName(name: String): VagrantVmConfigBuilder = {
    this.name = name
    this
  }

  def withHostOnlyIp(ip: String): VagrantVmConfigBuilder = {
    this.ip = ip
    this
  }

  def withLucid32Box: VagrantVmConfigBuilder = {
    this.boxName = "lucid32"
    try
      this.boxUrl = new URL("http://files.vagrantup.com/lucid32.box")
    catch {
      case e: MalformedURLException =>
        throw new RuntimeException(e)
    }
    this
  }

  def withLucid64Box: VagrantVmConfigBuilder = {
    this.boxName = "lucid64"
    try
      this.boxUrl = new URL("http://files.vagrantup.com/lucid64.box")
    catch {
      case e: MalformedURLException =>
        throw new RuntimeException(e)
    }
    this
  }

  /*

  SSP - Part !!!!!!!!!!!!!!!!!!!!!!!!!

   */

  def withDevBox: VagrantVmConfigBuilder = {
    this.boxName = "sds-devbox"
    try
      this.boxUrl = new URL("https://sds.ssp-europe.eu/api/v4/public/shares/downloads/YgegiC6hF3mtmSFoQ6IaLfF5maaIRGYm/5rogL8eq-fr2ZYhO6jW1XMxbbZxlaDw0L5gW7lj4v_mqMIssU1Ie79BRj9hHVDpBhcOU3YWWbELPUcYCOKAu47t4_y_8Mv-rmEnJLYbC7DDVz51is9AOd9VE5zdeU5ym4_HcGnX4TBLYCTwn3RiIcDo_EwCPnJ0h_RiKLG8_d26dgTohAl_KMYHQ1e4f18df7e302f02")
    catch {
      case e: MalformedURLException =>
        throw new RuntimeException(e)
    }
    this
  }

  /*

  SSP - Part !!!!!!!!!!!!!!!!!!!!!!!!!

 */

  def withBoxName(boxName: String): VagrantVmConfigBuilder = {
    this.boxName = boxName
    this
  }

  def withBoxUrl(boxUrl: URL): VagrantVmConfigBuilder = {
    this.boxUrl = boxUrl
    this
  }

  def build: VagrantVmConfig = {
    if (boxName == null) throw new VagrantBuilderException("No boxName defined")
    new VagrantVmConfig(name, ip, hostName, boxName, boxUrl, portForwardings, puppetProvisionerConfig, guiMode)
  }
}
