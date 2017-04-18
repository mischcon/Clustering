package vm
import java.io.File

import akka.actor.{ActorSystem, Props}
import communication.{GetRequest, HttpResponse}
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import vm.vagrant.Vagrant
import vm.vagrant.configuration.{Protocol, Service}
import vm.vagrant.configuration.builder._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._



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
  var vmConfig2 = VagrantVmConfigBuilder
    .create
    .withName("Test-VM2")
    .withHostName("Test-VM.pc-ziegert.local")
    .withBoxName("centos/7")
    .withBoxCheckUpdate(true)
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
    .withPath(new File("/Volumes/Daten/Vagrant/scala.local"))
    .build
  val vagrant = new Vagrant().createEnvironment(environmentConfig)
  //vagrant.up()
  //println(vagrant.up())
  //val test = vagrant.status()
  //println(test.mkString("Status:\n", "\n", ""))
  //println(vagrant.destroy())
  //vmConfig = vagrant.getBoxePortMapping(vmConfig)
  //vmConfig2 = vagrant.getBoxePortMapping(vmConfig2)
  //println(vagrant.sshExecute(vmConfig, "echo 'Test'"))
  //println(vagrant.sshExecute(vmConfig2, "echo 'Test2'"))
  //implicit val format = Serialization.formats(NoTypeHints)
  //val ser = write(environmentConfig)

  //println(ser)







}

class TestActorSystem {
  val system = ActorSystem("testActorSystem")
  val vmProxyActor = system.actorOf(Props[VMProxyActor], name="vmProxyActor")
  val vmActor = system.actorOf(Props[VMActor], name="vmActor")
  implicit val timeout = Timeout(100.days)


  val test = new GetRequest("https://.pc-ziegert.de")
  vmProxyActor ! vmActor
  val response = Await.result(vmProxyActor ? test, Duration.Inf).asInstanceOf[HttpResponse]

  println(response.getBody)

  println("Test")

  system.terminate()
}

object test extends App{
  new test
  //new TestActorSystem
}
