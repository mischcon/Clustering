package vm.vagrant.configuration.builder;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */

public class VagrantProvisionerConfigBuilder implements Serializable {
    public static VagrantProvisionerFileConfigBuilder createFileConfig() {
        return new VagrantProvisionerFileConfigBuilder();
    }

    public static VagrantProvisionerShellConfigBuilder createShellConfig() {
        return new VagrantProvisionerShellConfigBuilder();
    }
}

