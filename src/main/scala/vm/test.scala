package vm
import java.io.File

import vm.vagrant.Vagrant
import vm.vagrant.configuration.Protocol
import vm.vagrant.configuration.builder._



/**
  * Created by oliver.ziegert on 22.03.2017.
  */

class test {



  var vmConfig = VagrantVmConfigBuilder
    .create
    .withName("Test-VM")
    .withHostName("Test-VM.pc-ziegert.local")
    .withBoxName("centos/7")
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPortForwardingConfig
      .withName("Test-PortForwarding")
      .withAutoCorrect(true)
      .withGuestPort(1337)
      .withHostIp("127.0.0.1")
      .withHostPort(1337)
      .withProtocol(Protocol.tcp)
      .build)
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPrivateNetworkConfig
      .withAutoConfig(false)
      .withDhcp(true)
      .build)
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPrivateNetworkConfig
      .withDhcp(false)
      .withIp("192.168.10.200")
      .withNetmask("255.255.255.0")
      .build)
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPublicNetworkConfig
      .withAutoConfig(true)
      .withDhcp(true)
      .withBridge("en0: Ethernet")
      .build)
    .withVagrantSyncedFoldersConfig(VagrantSyncedFoldersConfigBuilder
      .createVirtualBoxConfig
      .withCreate(true)
      .withName("Test")
      .withHostPath("/Volumes/Daten/Vagrant/scala.test/share")
      .withGuestPath("/share")
      .build)
    .withGuiMode(false)
    .withBootTimeout(120)
    .withBoxCheckUpdate(true)
    .withCommunicator("ssh")
    .withPostUpMessage("Alles Geil!!")
    .withProvider(VagrantProviderConfigBuilder
      .create
      .withName("virtualbox")
      .withGuiMode(false)
      .withMemory(4096)
      .withCpus(2)
      .withVmName("Test-VM")
      .build())
    .build
  val vmConfig2 = VagrantVmConfigBuilder
    .create
    .withName("Test-VM2")
    .withHostName("Test-VM.pc-ziegert.local")
    .withBoxName("centos/7")
    .withBoxCheckUpdate(true)
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPortForwardingConfig
      .withName("Test-PortForwarding")
      .withAutoCorrect(true)
      .withGuestPort(1338)
      .withHostIp("127.0.0.1")
      .withHostPort(1338)
      .withProtocol(Protocol.tcp)
      .build)
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPrivateNetworkConfig
      .withAutoConfig(false)
      .withDhcp(true)
      .build)
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPrivateNetworkConfig
      .withDhcp(false)
      .withIp("192.168.10.201")
      .withNetmask("255.255.255.0")
      .build)
    .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
      .createPublicNetworkConfig
      .withAutoConfig(true)
      .withDhcp(true)
      .withBridge("en0: Ethernet")
      .build)
    .withVagrantSyncedFoldersConfig(VagrantSyncedFoldersConfigBuilder
      .createVirtualBoxConfig
      .withCreate(true)
      .withName("Test")
      .withHostPath("/Volumes/Daten/Vagrant/scala.test/share")
      .withGuestPath("/share")
      .build)
    .withGuiMode(false)
    .withBootTimeout(120)
    .withBoxCheckUpdate(true)
    .withCommunicator("ssh")
    .withPostUpMessage("Alles Geil!!")
    .withProvider(VagrantProviderConfigBuilder
      .create
      .withName("virtualbox")
      .withGuiMode(false)
      .withMemory(4096)
      .withCpus(2)
      .withVmName("Test-VM2").build())
    .withVagrantProvisionerConfig(VagrantProvisionerConfigBuilder
      .createShellConfig
      .withInline("echo 'Alles Toll!!'")
      .withName("Test-Inline")
      .build  )
    .build
  val environmentConfig = VagrantEnvironmentConfigBuilder
    .create
    .withVagrantVmConfig(vmConfig)
    .withVagrantVmConfig(vmConfig2)
    .build
  val vagrant = new Vagrant().createEnvironment(new File("/Volumes/Daten/Vagrant/scala.local"), environmentConfig)
  //vagrant.up()
  // println(vagrant.up)
  val test = vagrant.status()
  println(test.mkString("Status:\n", "\n", ""))
  //println(vagrant.destroy())
  vmConfig = vagrant.getBoxePortMapping(vmConfig)
  println(vagrant.sshExecute(vmConfig, "echo 'Test'"))
  println(vagrant.sshExecute(vmConfig2, "echo 'Test2'"))
}

object test extends App{
  new test
}