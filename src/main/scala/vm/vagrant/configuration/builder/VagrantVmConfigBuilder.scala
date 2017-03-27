package vm.vagrant.configuration.builder

import java.io.File

import scala.collection.immutable.List
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID

import vm.vagrant.configuration.{PuppetProvisionerConfig, VagrantPortForwarding, VagrantProviderConfig, VagrantVmConfig}
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantVmConfigBuilder {
  def create = new VagrantVmConfigBuilder
}

class VagrantVmConfigBuilder() {
  private var name: String =UUID.randomUUID.toString
  private var ip: String = _
  private var hostName: String = _
  private var boxName: String = _
  private var boxUrl: URL = _
  private var portForwardings: List[VagrantPortForwarding] = _
  private var puppetProvisionerConfig: PuppetProvisionerConfig = _
  private var guiMode: Boolean = false
  private var bootTimeout: Int = 300
  private var boxCheckUpdate: Boolean = true
  private var boxDownloadChecksum: String = _
  private var boxDownloadChecksumType:String = _
  private var boxDownloadClientCert: File = _
  private var boxDownloadCaCert: File = _
  private var boxDownloadCaPath: File = _
  private var boxDownloadInsecure: Boolean = false
  private var boxDownloadLocationTrusted: Boolean = false
  private var boxVersion: String = _
  private var communicator:String = _
  private var gracefulHaltTimeout: Int = 60
  private var guest: String = _
  private var postUpMessage: String = _
  private var usablePortRange: String = "2200..2250"
  private var provider: VagrantProviderConfig = new VagrantProviderConfig(memory = 2048, cpus = 2, customize = null, vmName = null)


  def withPuppetProvisionerConfig(puppetProvisionerConfig: PuppetProvisionerConfig): VagrantVmConfigBuilder = {
    this.puppetProvisionerConfig = puppetProvisionerConfig
    this
  }

  def withVagrantPortForwarding(portForwarding: VagrantPortForwarding): VagrantVmConfigBuilder = {
    portForwardings ::= portForwarding
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
    new VagrantVmConfig(name, ip, hostName, boxName, boxUrl, portForwardings, puppetProvisionerConfig, guiMode, bootTimeout, boxCheckUpdate, boxDownloadChecksum, boxDownloadChecksumType, boxDownloadClientCert, boxDownloadCaCert, boxDownloadCaPath, boxDownloadInsecure, boxDownloadLocationTrusted, boxVersion, communicator, gracefulHaltTimeout, guest, postUpMessage, usablePortRange, provider)
  }
}
