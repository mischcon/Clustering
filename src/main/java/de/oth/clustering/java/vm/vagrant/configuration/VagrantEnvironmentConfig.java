package de.oth.clustering.java.vm.vagrant.configuration;

import de.oth.clustering.java.utils.DeployInfoInterface;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the configuration of a Vagrant environmant.
 *
 * @author oliver.ziegert
 */
public class VagrantEnvironmentConfig implements Serializable, DeployInfoInterface {

    private List<VagrantVmConfig> vmConfigs;
    private File path;
    private String version;

    public VagrantEnvironmentConfig(Iterable<VagrantVmConfig> vmConfigs, File path) {
        this.vmConfigs = new ArrayList<>();
        if (vmConfigs != null) {
            for (VagrantVmConfig vagrantVmConfig : vmConfigs) {
                this.vmConfigs.add(vagrantVmConfig);
            }
        }
        this.path = path;
    }

    public VagrantEnvironmentConfig(Iterable<VagrantVmConfig> vmConfigs) {
        this(vmConfigs, new File("."));
    }

    /**
     * Returns all {@link VagrantVmConfig}
     *
     * @return all {@link VagrantVmConfig}
     */
    public Iterable<VagrantVmConfig> vmConfigs() {
        return vmConfigs;
    }

    public File path() { return path; }

    @Override
    public String version() { return version; }

    public void setVersion(String version) { this.version = version; }

    /**
     * Returns true if this configuration describes a multi VM environment. A multi VM environment manages more than one VM.
     *
     * @return true if this configuration describes a multi VM environment
     */
    public boolean isMultiVmEnvironment() {
        if (vmConfigs.size() > 1) {
            return true;
        }
        return false;
    }
}
