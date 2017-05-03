package vm.vagrant.configuration;

import vm.vagrant.util.Run;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface VagrantProvisionerConfig {
    String mode();
    Run run();
    Boolean preserveOrder();
}