package vm.vagrant.configuration.builder;

/**
 * Created by oliver.ziegert on 20.04.17.
 */

public class VagrantProvisionerConfigBuilder {
    public static VagrantProvisionerFileConfigBuilder createFileConfig() {
        return new VagrantProvisionerFileConfigBuilder();
    }

    public static VagrantProvisionerShellConfigBuilder createShellConfig() {
        return new VagrantProvisionerShellConfigBuilder();
    }
}

