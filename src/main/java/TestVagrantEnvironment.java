import vm.VmEnvironment;
import vm.vagrant.configuration.VagrantEnvironmentConfig;
import vm.vagrant.configuration.VagrantVmConfig;
import vm.vagrant.configuration.builder.*;
import vm.vagrant.util.Protocol;
import vm.vagrant.util.Service;

import java.io.File;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class TestVagrantEnvironment implements VmEnvironment {

    public VagrantEnvironmentConfig createEnvironment() {
        VagrantVmConfig vmConfig1 = VagrantVmConfigBuilder
            .create()
            .withName("Test-VM")
            .withHostName("Test-VM.pc-ziegert.local")
            .withBoxName("centos/7")
            .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
                .createPortForwardingConfig()
                .withName("Test-PortForwarding")
                .withAutoCorrect(true)
                .withGuestPort(1337)
                .withHostIp("127.0.0.1")
                .withHostPort(1337)
                .withProtocol(Protocol.TCP)
                .withService(Service.HTTPS)
                .build())
            .withVagrantSyncedFolderConfig(VagrantSyncedFoldersConfigBuilder
                .createVirtualBoxConfig()
                .withCreate(true)
                .withName("Test")
                .withHostPath("/Volumes/Daten/Vagrant/scala.local/share")
                .withGuestPath("/share")
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
            .build();
        VagrantVmConfig vmConfig2 = VagrantVmConfigBuilder
            .create()
            .withName("Test-VM2")
            .withHostName("Test-VM.pc-ziegert.local")
            .withBoxName("centos/7")
            .withBoxCheckUpdate(true)
            .withVagrantNetworkConfig(VagrantNetworkConfigBuilder
                .createPortForwardingConfig()
                .withName("Test-PortForwarding")
                .withAutoCorrect(true)
                .withGuestPort(1337)
                .withHostIp("127.0.0.1")
                .withHostPort(1337)
                .withProtocol(Protocol.TCP)
                .withService(Service.HTTP)
                .build())
            .withVagrantSyncedFolderConfig(VagrantSyncedFoldersConfigBuilder
                .createVirtualBoxConfig()
                .withCreate(true)
                .withName("Test")
                .withHostPath("/Volumes/Daten/Vagrant/scala.local/share")
                .withGuestPath("/share")
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
                .withVmName("Test-VM2").build())
            .withVagrantProvisionerConfig(VagrantProvisionerConfigBuilder
                .createShellConfig()
                .withInline("echo 'Alles Toll!!'")
                .withName("Test-Inline")
                .build())
            .build();
        VagrantEnvironmentConfig environmentConfig = VagrantEnvironmentConfigBuilder
                .create()
                .withVagrantVmConfig(vmConfig1)
                .withVagrantVmConfig(vmConfig2)
                .withPath(new File("/Volumes/Daten/Vagrant/scala.local"))
                .build();
        return environmentConfig;
    }
}
