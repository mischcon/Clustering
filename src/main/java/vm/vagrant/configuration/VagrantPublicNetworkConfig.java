package vm.vagrant.configuration;

import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantPublicNetworkConfig implements VagrantNetworkConfig{
    private Boolean dhcp;
    private Boolean useDhcpAssignedDefaultRoute;
    private String ip;
    private List<String> bridges;
    private Boolean autoConfig;

    public VagrantPublicNetworkConfig(Boolean dhcp, Boolean useDhcpAssignedDefaultRoute, String ip, List<String> bridges, Boolean autoConfig) {
        this.dhcp = dhcp;
        this.useDhcpAssignedDefaultRoute = useDhcpAssignedDefaultRoute;
        this.ip = ip;
        this.bridges = bridges;
        this.autoConfig = autoConfig;
    }

    public Boolean dhcp() {
        return dhcp;
    }

    public Boolean useDhcpAssignedDefaultRoute() {
        return useDhcpAssignedDefaultRoute;
    }

    public String ip() {
        return ip;
    }

    public List<String> bridges() {
        return bridges;
    }

    public Boolean autoConfig() {
        return autoConfig;
    }

    @Override
    public String mode() {
        return "public_network";
    }
}
