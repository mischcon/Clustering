package vm.vagrant.util

import vm.vagrant.configuration._

import scala.collection.JavaConverters._

/**
  * Created by oliver.ziegert on 04.04.17.
  */
object VagrantVmConfigUtils {

  def updatePortMapping(vmConfig: VagrantVmConfig, portMappings: Iterator[(Int, Int)]): VagrantVmConfig = {
    portMappings.foreach(portMapping => {
      val portForwards = vmConfig.vagrantNetworkConfigs().asScala.filter({ case x: VagrantPortForwardingConfig => x.guestPort == portMapping._1 && x.protocol == Protocol.TCP }).map(x => x.asInstanceOf[VagrantPortForwardingConfig])
      if (portForwards.isEmpty && portMapping._1 == 22) {
        vmConfig.vagrantNetworkConfigs().add(new VagrantPortForwardingConfig(false,
          portMapping._1,
          null,
          portMapping._2,
          "127.0.0.1",
          Protocol.TCP,
          Service.SSH,
          "ssh"));
      } else {
        portForwards.foreach(portForward => portForward.setHostPort(portMapping._2))
      }
    })
    vmConfig
  }

}
