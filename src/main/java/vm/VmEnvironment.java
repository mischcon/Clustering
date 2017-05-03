package vm;

import vm.vagrant.configuration.VagrantEnvironmentConfig;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface VmEnvironment extends Serializable {

    VagrantEnvironmentConfig createEnvironment();

}
