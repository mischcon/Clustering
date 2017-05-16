package de.oth.clustering.scala.vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.URL

import de.oth.clustering.scala.vm.vagrant.util.{ChecksumType, Protocol, Run}

import scala.collection.JavaConverters._


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
    for (vmConfig <- config.vmConfigs().asScala) {
      builder.append(createVmInMultiEnvConfig(vmConfig))
    }
    builder.append("end").append("\n")
    builder.toString
  }

  private def createVmInMultiEnvConfig(vmConfig: VagrantVmConfig) = {
    val builder = new StringBuilder
    builder.append(s"""  config.de.oth.de.oth.clustering.java.clustering.scala.vm.define "${vmConfig.name}" do |de.oth.de.oth.clustering.java.clustering.scala.vm|""").append("\n")
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
    if (vmConfig.vagrantNetworkConfigs != null) for (vagrantNetworkConfig <- vmConfig.vagrantNetworkConfigs().asScala) builder.append(createVmNetworkConfig(vagrantNetworkConfig))
    if (vmConfig.postUpMessage != null) builder.append(createVmPostUpMessageConfig(vmConfig.postUpMessage))
    builder.append(createVmProviderConfig(vmConfig.provider))
    if (vmConfig.vagrantProvisionerConfigs != null) for (vagrantProvisionerConfig <- vmConfig.vagrantProvisionerConfigs().asScala) builder.append(createVmProvisionerConfig(vagrantProvisionerConfig))
    if (vmConfig.vagrantSyncedFolderConfigs != null) for (vagrantSyncedFolderConfig <- vmConfig.vagrantSyncedFolderConfigs().asScala) builder.append(createVmSyncFolderConfig(vagrantSyncedFolderConfig))
    if (!vmConfig.usablePortRange.equals("2200..2250")) builder.append(createVmUsablePortRangeConfig(vmConfig.usablePortRange))
    builder.append("  end").append("\n")
    builder.toString
  }

  private def createVmBootTimeoutConfig(value: Int) = {
    val builder = new StringBuilder
    builder.append(s"    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.boot_timeout = ${value}").append("\n")
    builder.toString
  }

  private def createVmBoxNameConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxCheckUpdateConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_check_update = $value""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadChecksumConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_download_checksum = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadChecksumTypeConfig(value: ChecksumType) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_download_checksum_type = "${value.toString}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadClientCertConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_download_client_cert = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadCaCertConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_download_ca_cert = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadCaPathConfig(value: File) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_download_ca_path = "${value.getAbsolutePath}"""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadInsecureConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_download_insecure = $value""").append("\n")
    builder.toString
  }

  private def createVmBoxDownloadLocationTrustedConfig(value: Boolean) = {
    val builder = new StringBuilder
    builder.append(s"    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_download_location_trusted = ${value}").append("\n")
    builder.toString
  }

  private def createVmBoxUrlConfig(value: URL) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_url = "$value"""").append("\n")
    builder.toString
  }

  private def createVmBoxVersionConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.box_version = "$value"""").append("\n")
    builder.toString
  }

  private def createVmCommunicatorConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.communicator = "$value"""").append("\n")
    builder.toString
  }

  private def createVmGracefulHaltTimeoutConfig(value: Int) = {
    val builder = new StringBuilder
    builder.append(s"    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.graceful_halt_timeout = $value").append("\n")
    builder.toString
  }

  private def createVmGuestConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.guest = "$value"""").append("\n")
    builder.toString
  }

  private def createVmHostNameConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.hostname = "$value"""").append("\n")
    builder.toString
  }

  private def createVmPostUpMessageConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.post_up_message = "$value"""").append("\n")
    builder.toString
  }

  private def createVmUsablePortRangeConfig(value: String) = {
    val builder = new StringBuilder
    builder.append(s"    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.usable_port_range = $value").append("\n")
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
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.network "${privateNetwork.mode}"""")
    if (privateNetwork.dhcp) builder.append(s""", type: "dhcp" """)
    if (privateNetwork.ip != null && !privateNetwork.ip.isEmpty) builder.append(s""", ip: "${privateNetwork.ip}" """)
    if (privateNetwork.netmask != null && !privateNetwork.netmask.isEmpty) builder.append(s""", netmask: "${privateNetwork.netmask}" """)
    if (!privateNetwork.autoConfig) builder.append(s", auto_config: ${privateNetwork.autoConfig.toString} ")
    builder.append("\n")
    builder.toString
  }

  private def createVmNetworkPortForwardingConfig(portForwarding: VagrantPortForwardingConfig) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.network "${portForwarding.mode}", guest: ${portForwarding.guestPort}, host: ${portForwarding.hostPort}""")
    if (portForwarding.name != null && !portForwarding.name.isEmpty) builder.append(s""", id: "${portForwarding.name}"""")
    if (portForwarding.protocol != Protocol.TCP) builder.append(s""", protocol: "${portForwarding.protocol.toString}"""")
    if (portForwarding.autoCorrect) builder.append(s", auto_correct: ${portForwarding.autoCorrect}")
    if (portForwarding.guestIp != null && !portForwarding.guestIp.isEmpty) builder.append(s""", guest_ip: "${portForwarding.guestIp}"""")
    if (portForwarding.hostIp != null && !portForwarding.hostIp.isEmpty) builder.append(s""", host_ip: "${portForwarding.hostIp}"""")
    builder.append("\n")
    builder.toString
  }

  private def createVmNetworkPublicNetworkConfig(publicNetwork: VagrantPublicNetworkConfig) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.network "${publicNetwork.mode}"""")
    if (publicNetwork.useDhcpAssignedDefaultRoute) builder.append(s", use_dhcp_assigned_default_route: ${publicNetwork.useDhcpAssignedDefaultRoute.toString}")
    if (publicNetwork.ip != null && !publicNetwork.ip.isEmpty) builder.append(s""", ip: "${publicNetwork.ip}"""")
    if (publicNetwork.bridges != null && publicNetwork.bridges().size() > 0) {
      builder.append(", bridge:")
      if (publicNetwork.bridges().size() == 1 ) builder.append(s""" "${publicNetwork.bridges().get(0)}"""")
      else {
        builder.append(publicNetwork.bridges().asScala.mkString(" [ ", ", ", " ]"))
      }
    }
    if (!publicNetwork.autoConfig) builder.append(s", auto_config: ${publicNetwork.autoConfig.toString}")
    builder.append("\n")
    builder.toString
  }

  private def createVmSyncFolderConfig(syncFolder: VagrantSyncedFolderConfig) = {
    var builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.synced_folder "${syncFolder.hostPath}", "${syncFolder.guestPath}", type: "${syncFolder.mode}" """)
    if (syncFolder.create) builder.append(s", create: ${syncFolder.create.toString}")
    if (syncFolder.disabled) builder.append(s", disabled: ${syncFolder.disabled.toString}")
    if (syncFolder.group != null && !syncFolder.group.isEmpty) builder.append(s""", group: "${syncFolder.group}" """)
    if (syncFolder.mountOptions != null && syncFolder.mountOptions().size() > 0) builder.append(s""", mount_options: ${syncFolder.mountOptions().asScala.mkString("""["""", """", """", """"]""")} """)
    if (syncFolder.owner != null && !syncFolder.owner.isEmpty) builder.append(s""", owner: "${syncFolder.owner}" """)
    if (syncFolder.name != null && !syncFolder.name.isEmpty) builder.append(s""", id: "${syncFolder.name}" """)
    syncFolder match {
      case x: VagrantSyncedFolderNfsConfig => builder.append(createVmSyncedFolderNfsConfig(x.asInstanceOf[VagrantSyncedFolderNfsConfig]))
      case _ =>
    }
    builder.append("\n")
    builder.toString
  }

  private def createVmSyncedFolderNfsConfig(nfsConfig: VagrantSyncedFolderNfsConfig) = {
    val builder = new StringBuilder
    if (!nfsConfig.nfsExport) builder.append(s", nfs_export: ${nfsConfig.nfsExport.toString}")
    if (!nfsConfig.nfsUdp) builder.append(s", nfs_udp : ${nfsConfig.nfsUdp.toString}")
    if (!nfsConfig.nfsUdp) builder.append(s", nfs_udp : ${nfsConfig.nfsUdp.toString}")
    if (nfsConfig.nfsVersion != 3) builder.append(s", nfs_version: ${nfsConfig.nfsVersion.toString}")
    builder.toString
  }

  private def createVmProviderConfig(provider: VagrantProviderConfig) = {
    val builder = new StringBuilder
    if (provider.name != null && !provider.name.isEmpty) {
      builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.provider "${provider.name}" do |provider|""").append("\n")
      if (provider.guiMode) builder.append(s"      provider.gui = ${provider.guiMode}").append("\n")
      if (provider.memory > 0 ) builder.append(s"""      provider.memory = "${provider.memory.toString}"""").append("\n")
      if (provider.cpus > 0 ) builder.append(s"""      provider.cpus = ${provider.cpus.toString}""").append("\n")
      if (provider.vmName != null && !provider.vmName.isEmpty) builder.append(s"""      provider.name = "${provider.vmName}"""").append("\n")
      if (provider.customize != null) for (customize <- provider.customize().asScala) { builder.append(s"      provider.customize = ${customize}").append("\n") }
      builder.append("    end").append("\n")
    }
    builder.toString
  }

  private def createVmProvisionerConfig(provisioner: VagrantProvisionerConfig) = {
    val builder = new StringBuilder
    builder.append(s"""    de.oth.de.oth.clustering.java.clustering.scala.vm.de.oth.de.oth.clustering.java.clustering.scala.vm.provision "${provisioner.mode}"""")
    if (provisioner.run != Run.ONCE) builder.append(s""", run: "${provisioner.run.toString}"""")
    if (provisioner.preserveOrder) builder.append(s", preserve_order: ${provisioner.preserveOrder}")
    provisioner match {
      case x: VagrantProvisionerFileConfig => builder.append(createVmProvisionerFileConfig(x))
      case x: VagrantProvisionerShellConfig => builder.append(createVmProvisionerShellConfig(x))
    }
    builder.append("\n")
    builder.toString()
  }

  private def createVmProvisionerFileConfig(provisionerFile: VagrantProvisionerFileConfig) = {
    val builder = new StringBuilder
    if (provisionerFile.source != null) builder.append(s""", source: "${provisionerFile.source.toString}"""")
    if (provisionerFile.destination() != null) builder.append(s""", destination: "${provisionerFile.destination().toString}"""")
    builder.toString()
  }

  private def createVmProvisionerShellConfig(provisionerShell: VagrantProvisionerShellConfig) = {
    val builder = new StringBuilder
    if (provisionerShell.args != null && provisionerShell.args().size() > 0) builder.append(s""", args: ${provisionerShell.args().asScala.mkString("""["""", """", """", """"]""")}""")
    if (provisionerShell.binary) builder.append(s", binary: ${provisionerShell.binary.toString} ")
    if (!provisionerShell.privileged) builder.append(s", privileged: ${provisionerShell.privileged.toString} ")
    if (provisionerShell.uploadPath != null) builder.append(s""", upload_path: "${provisionerShell.uploadPath.toString}" """)
    if (provisionerShell.keepColor) builder.append(s", keep_color: ${provisionerShell.keepColor.toString} ")
    if (provisionerShell.name != null && !provisionerShell.name.isEmpty) builder.append(s""", name: "${provisionerShell.name}"""")
    if (provisionerShell.md5 != null && !provisionerShell.md5.isEmpty) builder.append(s""", md5: "${provisionerShell.md5}"""")
    else if (provisionerShell.sha1 != null && !provisionerShell.sha1.isEmpty) builder.append(s""", sha1: "${provisionerShell.sha1}"""")
    if (provisionerShell.inline != null && !provisionerShell.inline.isEmpty) builder.append(s", inline: <<-SHELL\n${provisionerShell.inline}\n    SHELL")
    else if (provisionerShell.path != null) builder.append(s""", path: "${provisionerShell.path.toString}" """)
    builder.toString()
  }
}

