package vm.vagrant.util

import vm.vagrant.configuration._

/**
  * Created by oliver.ziegert on 04.04.17.
  */
object VagrantVmConfigUtils {

  def updatePortMapping(vmConfig: VagrantVmConfig, portMappings: Iterator[(Int, Int)]): VagrantVmConfig = {
    portMappings.foreach(portMapping => {
      val portForwards = vmConfig.vagrantNetworkConfigs.filter({case x:VagrantPortForwardingConfig => x.guestPort == portMapping._1 && x.protocol == Protocol.tcp}).map(x => x.asInstanceOf[VagrantPortForwardingConfig])
      if (portForwards.isEmpty && portMapping._1 == 22) {
        vmConfig.vagrantNetworkConfigs ::= new VagrantPortForwardingConfig(autoCorrect = false,
          guestPort = portMapping._1,
          guestIp = null,
          hostPort = portMapping._2,
          hostIp = "127.0.0.1",
          protocol = Protocol.tcp,
          service = Service.ssh,
          name = "ssh")
      } else {
        portForwards.foreach(portForward => portForward.hostPort = portMapping._2)
      }
    })
    vmConfig
  }

}
