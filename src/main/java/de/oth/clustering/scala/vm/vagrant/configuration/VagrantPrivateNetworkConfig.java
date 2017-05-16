package de.oth.clustering.scala.vm.vagrant.configuration;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantPrivateNetworkConfig implements VagrantNetworkConfig {
    private Boolean dhcp;
    private String ip;
    private String netmask;
    private Boolean autoConfig;

    public VagrantPrivateNetworkConfig(Boolean dhcp, String ip, String netmask, Boolean autoConfig) {
        this.dhcp = dhcp;
        this.ip = ip;
        this.netmask = netmask;
        this.autoConfig = autoConfig;
    }

    public Boolean dhcp() {
        return dhcp;
    }

    public String ip() {
        return ip;
    }

    public String netmask() {
        return netmask;
    }

    public Boolean autoConfig() {
        return autoConfig;
    }

    @Override
    public String mode() {
        return "private_network";
    }
}
