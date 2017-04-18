package vm

import java.io.File

import akka.actor.{Actor, ActorLogging}
import org.json4s.native.Serialization.read
import vm.vagrant.Vagrant
import vm.vagrant.configuration.{Protocol, Service, VagrantEnvironmentConfig}
import vm.vagrant.configuration.builder._

/**
  * Created by mischcon on 3/20/17.
  */
class VMActor extends Actor with ActorLogging {

  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _

  override def receive: Receive = {
    case "up" => up; sender() ! "Mkey!"
    case "destroy" => destroy; sender() ! "Mkey!"
    case x: String => parseVmConfig(x)
  }

  def parseVmConfig(vmConfigJson: String) = {
    //try {
    //  vagrantEnvironmentConfig = read[VagrantEnvironmentConfig](vmConfigJson)
   // }
  }

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
      .withService(Service.http)
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
      .withCpus(4)
      .withVmName("Test-VM")
      .build())
    .build
  val environmentConfig = VagrantEnvironmentConfigBuilder
    .create
    .withPath(new File("/Volumes/Daten/Vagrant/scala.local"))
    .withVagrantVmConfig(vmConfig)
    .build
  val vagrant = new Vagrant().createEnvironment(environmentConfig)


  def up = {
    vagrant.up()
    vmConfig = vagrant.getBoxePortMapping(vmConfig)
  }

  def destroy = {
    vagrant.destroy()
  }

  def halt = {
    vagrant.halt()
  }

}
