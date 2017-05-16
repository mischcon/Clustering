package de.oth.clustering.java.vm.vagrant.configuration;

import de.oth.clustering.java.vm.vagrant.util.Run;

import java.io.File;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantProvisionerFileConfig implements VagrantProvisionerConfig {
    private Run run;
    private Boolean preserveOrder;
    private File source;
    private File destination;

    public VagrantProvisionerFileConfig(Run run, Boolean preserveOrder, File source, File destination) {
        this.run = run;
        this.preserveOrder = preserveOrder;
        this.source = source;
        this.destination = destination;
    }

    @Override
    public Run run() {
        return run;
    }

    @Override
    public Boolean preserveOrder() {
        return preserveOrder;
    }

    public File source() {
        return source;
    }

    public File destination() {
        return destination;
    }

    @Override
    public String mode() {
        return "file";
    }
}
