package de.oth.clustering.java.example;

import de.oth.clustering.java.vm.vagrant.configuration.builder.*;
import de.oth.clustering.java.vm.VmEnvironment;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantEnvironmentConfig;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantVmConfig;
import de.oth.clustering.java.vm.vagrant.util.Protocol;

/**
 * <strong>Contains Test Vagrant-Environment for the clustertest</strong><br><br>
 *
 * TODO: remove or outsource to Documentation / Wiki
 */
public class TestVagrantEnvironment implements VmEnvironment {

    public VagrantEnvironmentConfig createEnvironment() {
        VagrantVmConfig vmConfig = VagrantVmConfigBuilder
            .create()
            .withName("Test-VM")
            .withHostName("Test-VM.pc-ziegert.local")
            .withBoxName("ubuntu/trusty64;")
            .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
                .createPortForwardingConfig()
                .withName("Test-PortForwarding")
                .withAutoCorrect(true)
                .withGuestPort(80)
                .withHostIp("127.0.0.1")
                .withHostPort(1337)
                .withProtocol(Protocol.TCP)
                .withService("http")
                .build())
            .withBootTimeout(120)
            .withBoxCheckUpdate(true)
            .withCommunicator("ssh")
            .withPostUpMessage("Alles Geil!!")
            .withProvider(VagrantProviderConfigBuilder
                .create()
                .withName("virtualbox")
                .withGuiMode(false)
                .withMemory(4096)
                .withCpus(2)
                .withVmName("Test-VM")
                .build())
                .withVagrantProvisionerConfig(VagrantProvisionerConfigBuilder
                    .createShellConfig()
                    .withInline("apt-get -y update; apt-get-y install apache2; systemctl start apache2")
                    .build())
            .build();
        VagrantEnvironmentConfig environmentConfig = VagrantEnvironmentConfigBuilder
                .create()
                .withVagrantVmConfig(vmConfig)
                .build();
        return environmentConfig;
    }
}
