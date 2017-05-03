package vm.vagrant.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Global configuration for a Vagrant environment that uses {@link VagrantFileTemplateConfiguration} for a Vagrant environment
 *
 * @author oliver.ziegert
 */

public class VagrantConfiguration implements Serializable {

    private VagrantEnvironmentConfig environmentConfig;
    private List<VagrantFileTemplateConfiguration> fileTemplateConfigurations;
    private List<VagrantFolderTemplateConfiguration> folderTemplateConfigurations;

    public VagrantConfiguration(VagrantEnvironmentConfig environmentConfig, Iterable<VagrantFileTemplateConfiguration> fileTemplateConfigurations, Iterable<VagrantFolderTemplateConfiguration> folderTemplateConfigurations) {
        this.environmentConfig = environmentConfig;
        this.fileTemplateConfigurations = new ArrayList<>();
        for (VagrantFileTemplateConfiguration fileTemplateConfiguration : fileTemplateConfigurations) {
            this.fileTemplateConfigurations.add(fileTemplateConfiguration);
        }
        this.folderTemplateConfigurations = new ArrayList<>();
        for (VagrantFolderTemplateConfiguration folderTemplateConfiguration : folderTemplateConfigurations) {
            this.folderTemplateConfigurations.add(folderTemplateConfiguration);
        }
    }

    /**
     * Returns the configuration of the Vagrant environment
     * @return the configuration of the Vagrant environment
     */
    public VagrantEnvironmentConfig environmentConfig() {
        return environmentConfig;
    }

    /**
     * Returns all {@link VagrantFileTemplateConfiguration} used by this configuration.
     * @return all {@link VagrantFileTemplateConfiguration}
     */
    public Iterable<VagrantFileTemplateConfiguration> fileTemplateConfigurations() {
        return fileTemplateConfigurations;
    }

    public Iterable<VagrantFolderTemplateConfiguration> folderTemplateConfigurations() {
        return folderTemplateConfigurations;
    }
}
