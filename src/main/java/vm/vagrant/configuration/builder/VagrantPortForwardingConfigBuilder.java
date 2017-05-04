package vm.vagrant.configuration.builder;

import vm.vagrant.configuration.VagrantNetworkConfig;
import vm.vagrant.configuration.VagrantPortForwardingConfig;
import vm.vagrant.util.Protocol;
import vm.vagrant.util.Service;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantPortForwardingConfigBuilder implements IVagrantNetworkConfigBuilder {
    private Boolean autoCorrect;
    private Integer guestPort;
    private String guestIp;
    private Integer hostPort;
    private String hostIp;
    private Protocol protocol;
    private String service;
    private String name;

    public VagrantPortForwardingConfigBuilder() {
        hostIp = "127.0.0.1";
    }

    public VagrantPortForwardingConfigBuilder withAutoCorrect(Boolean autoCorrect) {
        this.autoCorrect = autoCorrect;
        return this;
    }

    public VagrantPortForwardingConfigBuilder withGuestPort(Integer guestPort) {
        this.guestPort = guestPort;
        return this;
    }

    public VagrantPortForwardingConfigBuilder withGuestIp(String guestIp) {
        this.guestIp = guestIp;
        return this;
    }

    public VagrantPortForwardingConfigBuilder withHostPort(Integer hostPort) {
        this.hostPort = hostPort;
        return this;
    }

    public VagrantPortForwardingConfigBuilder withHostIp(String hostIp) {
        this.hostIp = hostIp;
        return this;
    }

    public VagrantPortForwardingConfigBuilder withProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public VagrantPortForwardingConfigBuilder withService(String service) {
        this.service = service;
        return this;
    }

    public VagrantPortForwardingConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public VagrantNetworkConfig build() {
        return new VagrantPortForwardingConfig(autoCorrect, guestPort, guestIp, hostPort, hostIp, protocol, service, name);
    }
}