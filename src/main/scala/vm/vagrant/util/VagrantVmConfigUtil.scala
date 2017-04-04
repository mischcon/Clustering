package vm.vagrant.util

import vm.vagrant.configuration.{VagrantNetworkConfig, VagrantPortForwardingConfig, VagrantVmConfig}

/**
  * Created by oliver.ziegert on 04.04.17.
  */
object VagrantVmConfigUtil {

  def updatePortMapping(vmConfig: VagrantVmConfig, portMapping: Iterator[(Int, Int)]): VagrantVmConfig = {
    portMapping.foreach(d => vmConfig.vagrantNetworkConfigs = vmConfig.vagrantNetworkConfigs.map({case x: VagrantPortForwardingConfig => if(x.guestPort == d._1) x.hostPort = d._2 else x case x => x}).collect({case x: VagrantNetworkConfig => x})      )
    vmConfig
  }

}
