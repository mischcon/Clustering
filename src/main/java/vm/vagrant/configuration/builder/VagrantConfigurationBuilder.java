package vm.vagrant.configuration.builder;

import vm.vagrant.configuration.VagrantConfiguration;
import vm.vagrant.configuration.VagrantEnvironmentConfig;
import vm.vagrant.configuration.VagrantFileTemplateConfiguration;
import vm.vagrant.configuration.VagrantFolderTemplateConfiguration;
import vm.vagrant.configuration.builder.util.VagrantBuilderException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VagrantConfigurationBuilder implements Serializable {

    private VagrantEnvironmentConfig environmentConfig;
    private List<VagrantFileTemplateConfiguration> fileTemplateConfigurations;
    private List<VagrantFolderTemplateConfiguration> folderTemplateConfigurations;

    public VagrantConfigurationBuilder() {
        fileTemplateConfigurations = new ArrayList<>();
        folderTemplateConfigurations = new ArrayList<>();
    }

    public static VagrantConfigurationBuilder create() {
        return new VagrantConfigurationBuilder();
    }

    public VagrantConfigurationBuilder withVagrantEnvironmentConfig(
            VagrantEnvironmentConfig environmentConfig) {
        this.environmentConfig = environmentConfig;
        return this;
    }

    public VagrantConfigurationBuilder withVagrantFileTemplateConfiguration(
            VagrantFileTemplateConfiguration fileTemplateConfiguration) {
        this.fileTemplateConfigurations.add(fileTemplateConfiguration);
        return this;
    }

    public VagrantConfigurationBuilder withVagrantFolderTemplateConfiguration(
            VagrantFolderTemplateConfiguration folderTemplateConfiguration) {
        this.folderTemplateConfigurations.add(folderTemplateConfiguration);
        return this;
    }

    public VagrantConfiguration build() {
        if (environmentConfig == null) {
            throw new VagrantBuilderException(
                    "No VagrantEnvironmentConfig defined");
        }
        return new VagrantConfiguration(environmentConfig,
                fileTemplateConfigurations,
                folderTemplateConfigurations);
    }
}
