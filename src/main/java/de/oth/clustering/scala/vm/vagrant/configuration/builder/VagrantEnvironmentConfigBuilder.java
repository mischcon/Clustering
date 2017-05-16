package de.oth.clustering.scala.vm.vagrant.configuration.builder;

import de.oth.clustering.scala.vm.vagrant.configuration.VagrantEnvironmentConfig;
import de.oth.clustering.scala.vm.vagrant.configuration.VagrantVmConfig;
import de.oth.clustering.scala.vm.vagrant.configuration.builder.util.VagrantBuilderException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VagrantEnvironmentConfigBuilder implements Serializable {

    private List<VagrantVmConfig> vmConfigs;
    private File path;

    public VagrantEnvironmentConfigBuilder() {
        vmConfigs = new ArrayList<>();
        this.path = new File(".");
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
            throw new VagrantBuilderException("No de.oth.clustering.scala.vm defined");
        }
        return new VagrantEnvironmentConfig(vmConfigs, path);
    }
}
