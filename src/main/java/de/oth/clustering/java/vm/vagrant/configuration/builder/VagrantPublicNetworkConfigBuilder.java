package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantNetworkConfig;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantPublicNetworkConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */

public class VagrantPublicNetworkConfigBuilder implements IVagrantNetworkConfigBuilder {
    private Boolean dhcp;
    private Boolean useDhcpAssignedDefaultRoute;
    private String ip;
    private List<String> bridges;
    private Boolean autoConfig;

    public VagrantPublicNetworkConfigBuilder() {
        bridges = new ArrayList<String>();
    }

    public VagrantPublicNetworkConfigBuilder withDhcp(Boolean dhcp) {
        this.dhcp = dhcp;
        return this;
    }

    public VagrantPublicNetworkConfigBuilder withUseDhcpAssignedDefaultRoute(Boolean useDhcpAssignedDefaultRoute) {
        this.useDhcpAssignedDefaultRoute = useDhcpAssignedDefaultRoute;
        return this;
    }

    public VagrantPublicNetworkConfigBuilder withIp(String ip) {
        this.ip = ip;
        return this;
    }

    public VagrantPublicNetworkConfigBuilder withBridge(String bridges) {
        this.bridges.add(bridges);
        return this;
    }

    public VagrantPublicNetworkConfigBuilder withAutoConfig(Boolean autoConfig) {
        this.autoConfig = autoConfig;
        return this;
    }

    @Override
    public VagrantNetworkConfig build() {
        return new VagrantPublicNetworkConfig(dhcp, useDhcpAssignedDefaultRoute, ip, bridges, autoConfig);
    }
}
