package vm.vagrant.configuration.builder;

import vm.vagrant.configuration.VagrantEnvironmentConfig;
import vm.vagrant.configuration.VagrantVmConfig;
import vm.vagrant.configuration.builder.util.VagrantBuilderException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VagrantEnvironmentConfigBuilder {

    private List<VagrantVmConfig> vmConfigs;
    private File path;

    public VagrantEnvironmentConfigBuilder() {
        vmConfigs = new ArrayList<>();
    }

    public static VagrantEnvironmentConfigBuilder create() {
        return new VagrantEnvironmentConfigBuilder();
    }

    public VagrantEnvironmentConfigBuilder withVagrantVmConfig(
            VagrantVmConfig vmConfig) {
        this.vmConfigs.add(vmConfig);
        return this;
    }

    public VagrantEnvironmentConfigBuilder withPath(File path) {
        this.path = path;
        return this;
    }

    public VagrantEnvironmentConfig build() {
        if (vmConfigs.isEmpty()) {
            throw new VagrantBuilderException("No vm defined");
        }
        return new VagrantEnvironmentConfig(vmConfigs, path);
    }
}
