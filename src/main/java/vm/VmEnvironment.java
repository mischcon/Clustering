package vm;

import vm.vagrant.configuration.VagrantEnvironmentConfig;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface VmEnvironment {

    VagrantEnvironmentConfig createEnvironment();

}
