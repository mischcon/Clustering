package vm.vagrant.configuration.builder

import java.io.File

import scala.collection.immutable.List
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID

import vm.vagrant.configuration._
import vm.vagrant.configuration.ChecksumType.ChecksumType
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantVmConfigBuilder {
  def create = new VagrantVmConfigBuilder
}

class VagrantVmConfigBuilder() {
  private var name: String = UUID.randomUUID.toString
  private var hostName: String = _
  private var boxName: String = _
  private var boxUrl: URL = _
  private var vagrantProvisionerConfigs: List[VagrantProvisionerConfig] = _
  private var vagrantNetworkConfigs: List[VagrantNetworkConfig] = _
  private var vagrantSyncedFolderConfigs: List[VagrantSyncedFolderConfig] = _
  private var guiMode: Boolean = false
  private var bootTimeout: Int = 300
  private var boxCheckUpdate: Boolean = true
  private var boxDownloadChecksum: String = _
  private var boxDownloadChecksumType: ChecksumType = _
  private var boxDownloadClientCert: File = _
  private var boxDownloadCaCert: File = _
  private var boxDownloadCaPath: File = _
  private var boxDownloadInsecure: Boolean = false
  private var boxDownloadLocationTrusted: Boolean = false
  private var boxVersion: String = _
  private var communicator: String = _
  private var gracefulHaltTimeout: Int = 60
  private var guest: String = _
  private var postUpMessage: String = _
  private var usablePortRange: String = "2200..2250"
  private var provider: VagrantProviderConfig = _


  def withName(name: String): VagrantVmConfigBuilder = {
    this.name = name
    this
  }

  def withHostName(hostName: String): VagrantVmConfigBuilder = {
    this.hostName = hostName
    this
  }

  def withBoxName(boxName: String): VagrantVmConfigBuilder = {
    this.boxName = boxName
    this
  }

  def withBoxUrl(boxUrl: URL): VagrantVmConfigBuilder = {
    this.boxUrl = boxUrl
    this
  }

  def withVagrantProvisionerConfig(vagrantProvisionerConfig: VagrantProvisionerConfig): VagrantVmConfigBuilder = {
    this.vagrantProvisionerConfigs ::= vagrantProvisionerConfig
    this
  }

  def withVagrantNetworkConfig(vagrantNetworkConfig: VagrantNetworkConfig): VagrantVmConfigBuilder = {
    this.vagrantNetworkConfigs ::= vagrantNetworkConfig
    this
  }

  def withVagrantSyncedFoldersConfig(vagrantSyncedFoldersConfig: VagrantSyncedFolderConfig): VagrantVmConfigBuilder = {
    this.vagrantSyncedFolderConfigs ::= vagrantSyncedFoldersConfig
    this
  }

  def withGuiMode(guiMode: Boolean): VagrantVmConfigBuilder = {
    this.guiMode = guiMode
    this
  }

  def withBootTimeout(bootTimeout: Int): VagrantVmConfigBuilder = {
    this.bootTimeout = bootTimeout
    this
  }

  def withBoxCheckUpdate(boxCheckUpdate: Boolean): VagrantVmConfigBuilder = {
    this.boxCheckUpdate = boxCheckUpdate
    this
  }

  def withBoxDownloadChecksum(boxDownloadChecksum: String): VagrantVmConfigBuilder = {
    this.boxDownloadChecksum = boxDownloadChecksum
    this
  }

  def withBoxDownloadChecksumType(boxDownloadChecksumType: ChecksumType): VagrantVmConfigBuilder = {
    this.boxDownloadChecksumType = boxDownloadChecksumType
    this
  }

  def withBoxDownloadClientCert(boxDownloadClientCert: File): VagrantVmConfigBuilder = {
    this.boxDownloadClientCert = boxDownloadClientCert
    this
  }

  def withBoxDownloadCaCert(boxDownloadCaCert: File): VagrantVmConfigBuilder = {
    this.boxDownloadCaCert = boxDownloadCaCert
    this
  }

  def withBoxDownloadCaPath(boxDownloadCaPath: File): VagrantVmConfigBuilder = {
    this.boxDownloadCaPath = boxDownloadCaPath
    this
  }

  def withBoxDownloadInsecure(boxDownloadInsecure: Boolean): VagrantVmConfigBuilder = {
    this.boxDownloadInsecure = boxDownloadInsecure
    this
  }

  def withBoxDownloadLocationTrusted(boxDownloadLocationTrusted: Boolean): VagrantVmConfigBuilder = {
    this.boxDownloadLocationTrusted = boxDownloadLocationTrusted
    this
  }

  def withBoxVersion(boxVersion: String): VagrantVmConfigBuilder = {
    this.boxVersion = boxVersion
    this
  }

  def withCommunicator(communicator: String): VagrantVmConfigBuilder = {
    this.communicator = communicator
    this
  }

  def withGracefulHaltTimeout(gracefulHaltTimeout: Int): VagrantVmConfigBuilder = {
    this.gracefulHaltTimeout = gracefulHaltTimeout
    this
  }

  def withGuest(guest: String): VagrantVmConfigBuilder = {
    this.guest = guest
    this
  }

  def withPostUpMessage(postUpMessage: String): VagrantVmConfigBuilder = {
    this.postUpMessage = postUpMessage
    this
  }

  def withUsablePortRange(usablePortRange: String): VagrantVmConfigBuilder = {
    this.usablePortRange = usablePortRange
    this
  }

  def withProvider(provider: VagrantProviderConfig): VagrantVmConfigBuilder = {
    this.provider = provider
    this
  }

  /*
   * SSP - Part !!!!!!!!!!!!!!!!!!!!!!!!!
   */

  def withSspDevBox: VagrantVmConfigBuilder = {
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
   * SSP - Part !!!!!!!!!!!!!!!!!!!!!!!!!
   */

  def build: VagrantVmConfig = {
    if (boxName == null) throw new VagrantBuilderException("No boxName defined")
    new VagrantVmConfig(name = name,
      hostName = hostName,
      boxName = boxName,
      boxUrl = boxUrl,
      vagrantProvisionerConfigs = vagrantProvisionerConfigs,
      vagrantNetworkConfigs = vagrantNetworkConfigs,
      vagrantSyncedFolderConfigs = vagrantSyncedFolderConfigs,
      guiMode = guiMode,
      bootTimeout = bootTimeout,
      boxCheckUpdate = boxCheckUpdate,
      boxDownloadChecksum = boxDownloadChecksum,
      boxDownloadChecksumType = boxDownloadChecksumType,
      boxDownloadClientCert = boxDownloadClientCert,
      boxDownloadCaCert = boxDownloadCaCert,
      boxDownloadCaPath = boxDownloadCaPath,
      boxDownloadInsecure = boxDownloadInsecure,
      boxDownloadLocationTrusted = boxDownloadLocationTrusted,
      boxVersion = boxVersion,
      communicator = communicator,
      gracefulHaltTimeout = gracefulHaltTimeout,
      guest = guest,
      postUpMessage = postUpMessage,
      usablePortRange = usablePortRange,
      provider = provider)
  }
}
