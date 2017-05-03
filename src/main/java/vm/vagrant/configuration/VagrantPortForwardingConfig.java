package vm.vagrant.configuration;

import vm.vagrant.util.Protocol;
import vm.vagrant.util.Service;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantPortForwardingConfig implements VagrantNetworkConfig {
    private Boolean autoCorrect;
    private Integer guestPort;
    private String guestIp;
    private Integer hostPort;
    private String hostIp;
    private Protocol protocol;
    private String service;
    private String name;

    public VagrantPortForwardingConfig(Boolean autoCorrect, Integer guestPort, String guestIp, Integer hostPort, String hostIp, Protocol protocol, String service, String name) {
        this.autoCorrect = autoCorrect;
        this.guestPort = guestPort;
        this.guestIp = guestIp;
        this.hostPort = hostPort;
        this.hostIp = hostIp;
        this.protocol = protocol;
        this.service = service;
        this.name = name;
    }

    public Boolean autoCorrect() {
        return autoCorrect;
    }

    public Integer guestPort() {
        return guestPort;
    }

    public String guestIp() {
        return guestIp;
    }

    public Integer hostPort() { return hostPort; }

    public void setHostPort(Integer hostPort) { this.hostPort = hostPort; }

    public String hostIp() {
        return hostIp;
    }

    public Protocol protocol() {
        return protocol;
    }

    public String service() {
        return service;
    }

    public String name() {
        return name;
    }

    @Override
    public String mode() {
        return "forwarded_port";
    }
}
