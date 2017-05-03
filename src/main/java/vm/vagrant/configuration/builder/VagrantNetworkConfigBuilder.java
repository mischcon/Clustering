package vm.vagrant.configuration.builder;

/**
 * Created by oliver.ziegert on 20.04.17.
 */


public class VagrantNetworkConfigBuilder {
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





