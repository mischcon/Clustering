package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.URL
import java.util.UUID

import vm.vagrant.configuration.ChecksumType.ChecksumType

/**
  * Some utilities for the configuration of Vagrant environments. This class creates configurationfiles for Vagrant.
  *
  * @author oliver.ziegert
  *
  */

object VagrantConfigurationUtilities {

  def createVagrantFileContent(config: VagrantEnvironmentConfig): String = {
    val builder = new StringBuilder
    builder.append("Vagrant.configure(\"2\") do |config|").append("\n")
    for (vmConfig <- config.getVmConfigs) {
      builder.append(createVmInMultiEnvConfig(vmConfig))
    }
    builder.append("end").append("\n")
    builder.toString
  }

  private def createVmInMultiEnvConfig(vmConfig: VagrantVmConfig) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.define "${vmConfig.name}" do |vm|""").append("\n")
    if (vmConfig.boxName != 300)builder.append(createVmBootTimeoutConfig(vmConfig.bootTimeout))
    if (vmConfig.boxName != null) builder.append(createVmBoxNameConfig(vmConfig.boxName))
    if (!vmConfig.boxCheckUpdate) builder.append(createVmBoxCheckUpdateConfig(vmConfig.boxCheckUpdate))
    if (vmConfig.boxDownloadChecksum != null) builder.append(createVmBoxDownloadChecksumConfig(vmConfig.boxDownloadChecksum))
    if (vmConfig.boxDownloadChecksumType != null) builder.append(createVmBoxDownloadChecksumTypeConfig(vmConfig.boxDownloadChecksumType))
    if (vmConfig.boxDownloadClientCert != null) builder.append(createVmBoxDownloadClientCertConfig(vmConfig.boxDownloadClientCert))
    if (vmConfig.boxDownloadCaCert != null) builder.append(createVmBoxDownloadCaCertConfig(vmConfig.boxDownloadCaCert))
    if (vmConfig.boxDownloadCaPath != null) builder.append(createVmBoxDownloadCaPathConfig(vmConfig.boxDownloadCaPath))
    if (vmConfig.boxDownloadInsecure) builder.append(createVmBoxDownloadInsecureConfig(vmConfig.boxDownloadInsecure))
    if (vmConfig.boxDownloadLocationTrusted) builder.append(createVmBoxDownloadLocationTrustedConfig(vmConfig.boxDownloadLocationTrusted))
    if (vmConfig.boxUrl != null) builder.append(createVmBoxUrlConfig(vmConfig.boxUrl))
    if (vmConfig.boxVersion != null) builder.append(createVmBoxVersionConfig(vmConfig.boxVersion))
    if (vmConfig.communicator != null) builder.append(createVmCommunicatorConfig(vmConfig.communicator))
    if (vmConfig.gracefulHaltTimeout != 0) builder.append(createVmGracefulHaltTimeoutConfig(vmConfig.gracefulHaltTimeout))
    if (vmConfig.guest != null) builder.append(createVmGuestConfig(vmConfig.guest))
    if (vmConfig.hostName != null) builder.append(createVmHostNameConfig(vmConfig.hostName))
    if (vmConfig.vagrantNetworkConfigs != null) for (vagrantNetworkConfig <- vmConfig.vagrantNetworkConfigs) {createVmNetworkConfig(vagrantNetworkConfig)}
    if (vmConfig.postUpMessage != null) builder.append(createVmPostUpMessageConfig(vmConfig.postUpMessage))
    builder.append(createVmProviderConfig(vmConfig.provider))
    //TODO: Provision
    //TODO: SyncFolder
    if (!vmConfig.usablePortRange.equals("2200..2250")) builder.append(createVmUsablePortRangeConfig(vmConfig.usablePortRange))
    builder.append("  end").append("\n")
    builder.toString
  }

  private def createVmBootTimeoutConfig(value: Int) = {
    val builder = new StringBuilder
    builder.append(s"    vm.vm.boot_timeout = ${value}").append("\n")
    builder.toString
  }

  private def createVmBoxNameConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxCheckUpdateConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_check_update = $value""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadChecksumConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_download_checksum = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadChecksumTypeConfig(value: ChecksumType) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_download_checksum_type = "${value.toString}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadClientCertConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_download_client_cert = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadCaCertConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_download_ca_cert = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadCaPathConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_download_ca_path = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadInsecureConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_download_insecure = $value""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadLocationTrustedConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"    vm.vm.box_download_location_trusted = ${value}").append("\n")
    builder.toString
  }

  private def createVmBoxUrlConfig(value: URL) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_url = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxVersionConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.box_version = "$value"""").append("\n")
    builder.toString
  }

  private def createVmCommunicatorConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.communicator = "$value"""").append("\n")
    builder.toString
  }

  private def createVmGracefulHaltTimeoutConfig(value: Int) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.graceful_halt_timeout = "$value"""").append("\n")
    builder.toString
  }

  private def createVmGuestConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.guest = "$value"""").append("\n")
    builder.toString
  }

  private def createVmHostNameConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.hostname = "$value"""").append("\n")
    builder.toString
  }

  private def createVmPostUpMessageConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.post_up_message = "$value"""").append("\n")
    builder.toString
  }

  // TODO: Fertigstellen

  private def createVmSyncedFolderConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.synced_folder = "$value"""").append("\n")
    builder.toString
  }

  private def createVmUsablePortRangeConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    vm.vm.usable_port_range = "$value"""").append("\n")
    builder.toString
  }

  private def createVmNetworkConfig(vagrantNetworkConfig: VagrantNetworkConfig): String = {
    vagrantNetworkConfig match {
      case x: VagrantPrivateNetworkConfig => createVmNetworkPrivateNetworkConfig(x.asInstanceOf[VagrantPrivateNetworkConfig])
      case x: VagrantPortForwardingConfig => createVmNetworkPortForwardingConfig(x.asInstanceOf[VagrantPortForwardingConfig])
      case x: VagrantPublicNetworkConfig  => createVmNetworkPublicNetworkConfig(x.asInstanceOf[VagrantPublicNetworkConfig])
    }

  }

  private def createVmNetworkPrivateNetworkConfig(privateNetwork: VagrantPrivateNetworkConfig) = {
    val builder = new StringBuilder
    if (privateNetwork.isComplete) {
      builder.append(s"""  vm.vm.network "${privateNetwork.mode}"""")
      if (privateNetwork.dhcp) builder.append(s""", type: "dhcp" """)
      if (privateNetwork.ip != null && !privateNetwork.ip.isEmpty) builder.append(s""", ip: "${privateNetwork.ip}" """)
      if (privateNetwork.netmask > 0) builder.append(s""", netmask: "${privateNetwork.netmask}" """)
      if (!privateNetwork.autoConfig) builder.append(s", auto_config: ${privateNetwork.autoConfig.toString} ")
      builder.append("\n")
    }
    builder.toString
  }

  private def createVmNetworkPortForwardingConfig(portForwarding: VagrantPortForwardingConfig) = {
    val builder = new StringBuilder
    if (portForwarding.isComplete) {
      builder.append(s"""  vm.vm.network "${portForwarding.mode}", guest: ${portForwarding.guestPort}, host: ${portForwarding.hostPort}""")
      if (portForwarding.name != null && !portForwarding.name.isEmpty) builder.append(s", id: ${portForwarding.name}")
      if (portForwarding.protocol != Protocol.tcp) builder.append(s", protocol: ${portForwarding.protocol.toString}")
      if (portForwarding.autoCorrect) builder.append(s", auto_correct: ${portForwarding.autoCorrect}")
      if (portForwarding.guestIp != null && !portForwarding.guestIp.isEmpty) builder.append(s""", guest_ip : "${portForwarding.guestIp}"""")
      if (portForwarding.hostIp != null && !portForwarding.hostIp.isEmpty) builder.append(s""", host_ip : "${portForwarding.hostIp}"""")
      builder.append("\n")
    }
    builder.toString
  }

  private def createVmNetworkPublicNetworkConfig(publicNetwork: VagrantPublicNetworkConfig) = {
    val builder = new StringBuilder
    if (publicNetwork.isComplete) {
      builder.append(s"""  vm.vm.network "${publicNetwork.mode}"""")
      if (publicNetwork.useDhcpAssignedDefaultRoute) builder.append(s", use_dhcp_assigned_default_route: ${publicNetwork.useDhcpAssignedDefaultRoute.toString}")
      if (publicNetwork.ip != null && !publicNetwork.ip.isEmpty) builder.append(s""", ip: "${publicNetwork.ip}"""")
      if (publicNetwork.bridges != null && publicNetwork.bridges.length > 0) {
        builder.append(", bridge:")
        if (publicNetwork.bridges.length == 1 ) builder.append(s""" "${publicNetwork.bridges{0}}"""")
        else {
          builder.append(publicNetwork.bridges.mkString(" [ ", ", ", " ]"))
        }
      }
      if (!publicNetwork.autoAonfig) builder.append(s", auto_config: ${publicNetwork.autoAonfig.toString}")
    }
    builder.toString
  }

  private def createVmProviderConfig(value: VagrantProviderConfig) = {
    val builder = new StringBuilder
    if (value.name != null && !value.name.isEmpty) {
      builder.append(s"""    vm.vm.provider "${value.name}" do |provider|""").append("\n")
      if (value.guiMode) builder.append(s"      provider.gui = ${value.guiMode}").append("\n")
      if (value.memory > 0 ) builder.append(s"""      provider.memory = "${value.memory}"""").append("\n")
      if (value.cpus > 0 ) builder.append(s"""      provider.cpus = "${value.cpus}"""").append("\n")
      if (value.vmName != null && !value.vmName.isEmpty) builder.append(s"""      provider.name = "${value.vmName}"""").append("\n")
      if (value.customize != null) for (customize <- value.customize) { builder.append(s"      provider.customize = ${customize}").append("\n") }
      builder.append("    end").append("\n")
    }
    builder.toString
  }

  private def createPuppetProvisionerConfig(vmConfigName: String, puppetProvisionerConfig: VagrantProvisionerConfig) = {
    val builder = new StringBuilder
    if (puppetProvisionerConfig != null) {
      builder.append(vmConfigName + ".vm.provision :puppet do |puppet|").append("\n")
      builder.append("puppet.manifests_path = \"" + puppetProvisionerConfig.getManifestsPath + "\"").append("\n")
      builder.append("puppet.manifest_file  = \"" + puppetProvisionerConfig.getManifestFile + "\"").append("\n")
      val modulesPath = puppetProvisionerConfig.getModulesPath
      if (modulesPath != null) builder.append("puppet.module_path  = \"" + modulesPath + "\"").append("\n")
      val debug = puppetProvisionerConfig.isDebug
      if (debug) builder.append("puppet.options  = \"--verbose --debug\"").append("\n")
      builder.append("end").append("\n")
    }
    builder.toString
  }
}

