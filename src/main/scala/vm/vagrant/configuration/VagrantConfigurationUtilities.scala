package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

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
    builder.append("Vagrant::Config.run do |config|").append("\n")
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
    builder.append("config.vm.define :" + vmName + " do |" + vmName + "_config|").append("\n")
    for (portForwarding <- vmConfig.getPortForwardings) {
      builder.append(createPortForwardingConfig(vmName + "_config", portForwarding))
    }
    builder.append(createBoxNameConfig(vmName + "_config", vmConfig.getBoxName))
    val boxUrl = vmConfig.getBoxUrl
    if (boxUrl != null) builder.append(createBoxUrlConfig(vmName + "_config", boxUrl))
    val ip = vmConfig.getIp
    if (ip != null) builder.append(createHostOnlyIpConfig(vmName + "_config", ip))
    val guiMode = vmConfig.isGuiMode
    if (guiMode) builder.append(createGuiModeConfig(vmName + "_config"))
    val hostName = vmConfig.getHostName
    if (hostName != null) builder.append(createHostNameConfig(vmName + "_config", hostName))
    val puppetProvisionerConfig = vmConfig.getPuppetProvisionerConfig
    if (puppetProvisionerConfig != null) builder.append(createPuppetProvisionerConfig(vmName + "_config", puppetProvisionerConfig))
    builder.append("end").append("\n")
    builder.toString
  }

  private def createPortForwardingConfig(vmConfigName: String, portForwarding: VagrantPortForwarding) = {
    val builder = new StringBuilder
    val portForwardingName = portForwarding.getName
    if (portForwardingName != null) builder.append(vmConfigName + ".vm.forward_port \"" + portForwardingName + "\", " + portForwarding.getGuestport + ", " + portForwarding.getHostport).append("\n")
    else builder.append(vmConfigName + ".vm.forward_port " + portForwarding.getGuestport + ", " + portForwarding.getHostport).append("\n")
    builder.toString
  }

  private def createBoxNameConfig(vmConfigName: String, boxName: String) = {
    val builder = new StringBuilder
    builder.append(vmConfigName + ".vm.box = \"" + boxName + "\"").append("\n")
    builder.toString
  }

  private def createHostNameConfig(vmConfigName: String, hostName: String) = {
    val builder = new StringBuilder
    if (hostName != null) builder.append(vmConfigName + ".vm.host_name = \"" + hostName + ".local\"").append("\n")
    builder.toString
  }

  private def createBoxUrlConfig(vmConfigName: String, boxUrl: URL) = {
    val builder = new StringBuilder
    if (boxUrl != null) builder.append(vmConfigName + ".vm.box_url = \"" + boxUrl + "\"").append("\n")
    builder.toString
  }

  private def createHostOnlyIpConfig(vmConfigName: String, ip: String) = {
    val builder = new StringBuilder
    if (ip != null) builder.append(vmConfigName + ".vm.network :hostonly, \"" + ip + "\"").append("\n")
    builder.toString
  }

  private def createGuiModeConfig(vmConfigName: String) = {
    val builder = new StringBuilder
    builder.append(vmConfigName + ".vm.boot_mode = :gui").append("\n")
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
