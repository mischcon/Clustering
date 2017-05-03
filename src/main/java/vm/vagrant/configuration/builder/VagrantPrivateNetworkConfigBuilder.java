package vm.vagrant.configuration.builder;

import vm.vagrant.configuration.VagrantNetworkConfig;
import vm.vagrant.configuration.VagrantPrivateNetworkConfig;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantPrivateNetworkConfigBuilder implements IVagrantNetworkConfigBuilder {
    private Boolean dhcp;
    private String ip;
    private String netmask;
    private Boolean autoConfig;

    public VagrantPrivateNetworkConfigBuilder() {
    }

    public VagrantPrivateNetworkConfigBuilder withDhcp(Boolean dhcp) {
        this.dhcp = dhcp;
        return this;
    }

    public VagrantPrivateNetworkConfigBuilder withIp(String ip) {
        this.ip = ip;
        return this;
    }

    public VagrantPrivateNetworkConfigBuilder withNetmask(String netmask) {
        this.netmask = netmask;
        return this;
    }

    public VagrantPrivateNetworkConfigBuilder withAutoConfig(Boolean autoConfig) {
        this.autoConfig = autoConfig;
        return this;
    }

    @Override
    public VagrantNetworkConfig build() {
        return new VagrantPrivateNetworkConfig(dhcp, ip, netmask, autoConfig);
    }
}
