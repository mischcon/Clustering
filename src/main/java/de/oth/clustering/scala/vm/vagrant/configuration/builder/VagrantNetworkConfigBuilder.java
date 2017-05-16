package de.oth.clustering.scala.vm.vagrant.configuration.builder;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */


public class VagrantNetworkConfigBuilder implements Serializable {
    public static VagrantPrivateNetworkConfigBuilder createPrivateNetworkConfig() {
        return new VagrantPrivateNetworkConfigBuilder();
    }

    public static VagrantPortForwardingConfigBuilder createPortForwardingConfig() {
        return new VagrantPortForwardingConfigBuilder();
    }

    public static VagrantPublicNetworkConfigBuilder createPublicNetworkConfig() {
        return new VagrantPublicNetworkConfigBuilder();
    }
}





