package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantProvisionerConfig;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantProvisionerFileConfig;
import de.oth.clustering.java.vm.vagrant.util.Run;

import java.io.File;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantProvisionerFileConfigBuilder implements IVagrantProvisionerConfigBuilder {
    private Run run;
    private Boolean preserveOrder;
    private File source;
    private File destination;

    public VagrantProvisionerFileConfigBuilder() {
        run = Run.ONCE;
        preserveOrder = false;
    }

    public VagrantProvisionerFileConfigBuilder withRun(Run run) {
        this.run = run;
        return this;
    }

    public VagrantProvisionerFileConfigBuilder withPreserveOrder(Boolean preserveOrder) {
        this.preserveOrder = preserveOrder;
        return this;
    }

    public VagrantProvisionerFileConfigBuilder withSource(File source) {
        this.source = source;
        return this;
    }

    public VagrantProvisionerFileConfigBuilder withDestination(File destination) {
        this.destination = destination;
        return this;
    }

    @Override
    public VagrantProvisionerConfig build() {
        return new VagrantProvisionerFileConfig(run, preserveOrder, source, destination);
    }
}
