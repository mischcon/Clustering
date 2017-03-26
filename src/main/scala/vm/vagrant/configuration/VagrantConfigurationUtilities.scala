package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.URL
import java.util.UUID

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
    var vmName = vmConfig.getName
    if (vmName == null) vmName = UUID.randomUUID.toString
    builder.append(createVmBootTimeoutConfig(vmConfig.getBootTimeout))
    val boxName = vmConfig.getBoxName
    if (boxName != null) builder.append(createVmBoxNameConfig(boxName))
    builder.append(createVmBoxCheckUpdateConfig(vmConfig.getBoxCheckUpdate))




    builder.append("end").append("\n")
    builder.toString
  }

  private def createVmBootTimeoutConfig(value: Int) = {
    val builder = new StringBuilder
    builder.append(s"  config.vm.boot_timeout = ${value}").append("\n")
    builder.toString
  }

  private def createVmBoxNameConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxCheckUpdateConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_check_update = $value""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadChecksumConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_download_checksum = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadChecksumTypeConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_download_checksum_type = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadClientCertConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_download_client_cert = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm. = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadCaCertConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_download_ca_cert = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadCaPathConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_download_ca_path = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadInsecureConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_download_insecure = $value""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadLocationTrustedConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"  config.vm.box_download_location_trusted = ${value}").append("\n")
    builder.toString
  }

  private def createVmBoxUrlConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_url = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxVersionConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.box_version = "$value"""").append("\n")
    builder.toString
  }

  private def createVmCommunicatorConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.communicator = "$value"""").append("\n")
    builder.toString
  }

  private def createVmGracefulHaltTimeoutConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.graceful_halt_timeout = "$value"""").append("\n")
    builder.toString
  }

  private def createVmGuestConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.guest = "$value"""").append("\n")
    builder.toString
  }

  private def createVmHostNameConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.hostname = "$value"""").append("\n")
    builder.toString
  }

  private def createVmPostUpMessageConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.post_up_message = "$value"""").append("\n")
    builder.toString
  }

  private def createVmProvisionConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.provision = "$value"""").append("\n")    // TODO: Fertigstellen
    builder.toString
  }

  private def createVmSyncedFolderConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm.synced_folder = "$value"""").append("\n")  // TODO: Fertigstellen
    builder.toString
  }

  private def createVmUsablePortRangeConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""  config.vm. = "$value"""").append("\n")
    builder.toString
  }

  private def createVmNetworkPortForwardingConfig(vmConfigName: String, portForwarding: VagrantPortForwarding) = {
    val builder = new StringBuilder
    if (portForwarding.isComplete) {
      builder.append(s"""  config.vm.network "forwarded_port", guest: ${portForwarding.getGuestport}, host: ${portForwarding.getHostport}""")
      val portForwardingName = portForwarding.getName
      if (portForwardingName != null && !portForwardingName.isEmpty) builder.append(s", id: $portForwardingName")
      val portForwardingProtocol = portForwarding.getpProtocol
      if (portForwardingProtocol != null && !portForwardingProtocol.isEmpty) builder.append(s", protocol: $portForwardingProtocol")
      builder.append("\n")
    }
    builder.toString
  }

  private def createVmProviderConfig(value: VagrantProviderConfig) = {
    val builder = new StringBuilder
    val providerName = value.getName
    if (providerName != null && providerName.isEmpty) {
      builder.append(s"""  config.vm.provider "$providerName" do |provider|""").append("\n")
      builder.append(s"    provider.gui = ${value.isGuiMode}").append("\n")
      val providerMemory = value.getMemory
      if (providerMemory < 0 ) builder.append(s"""    provider.memory = "$providerMemory"""").append("\n")
      val providerCpus = value.getCpus
      if (providerMemory < 0 ) builder.append(s"""    provider.cpus = "$providerCpus"""").append("\n")
      builder.append("  end").append("\n")
    }
    builder.toString
  }

  private def createHostOnlyIpConfig(vmConfigName: String, ip: String) = {
    val builder = new StringBuilder
    if (ip != null) builder.append(vmConfigName + ".vm.network :hostonly, \"" + ip + "\"").append("\n")
    builder.toString
  }

  private def createPuppetProvisionerConfig(vmConfigName: String, puppetProvisionerConfig: PuppetProvisionerConfig) = {
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

class VagrantConfigurationUtilities private() {
}
