package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantProviderConfig;
import de.oth.clustering.java.vm.vagrant.configuration.builder.util.VagrantBuilderException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantProviderConfigBuilder implements Serializable {
    private String name;
    private Boolean guiMode;
    private Integer memory;
    private Integer cpus;
    private List<String> customize;
    private String vmName;

    public VagrantProviderConfigBuilder() {
        this.customize = new ArrayList<String>();
        this.guiMode = false;
    }

    public static VagrantProviderConfigBuilder create() {
        return new VagrantProviderConfigBuilder();
    }

    public VagrantProviderConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public VagrantProviderConfigBuilder withGuiMode(Boolean guiMode) {
        this.guiMode = guiMode;
        return this;
    }

    public VagrantProviderConfigBuilder withMemory(Integer memory) {
        this.memory = memory;
        return this;
    }

    public VagrantProviderConfigBuilder withCpus(Integer cpus) {
        this.cpus = cpus;
        return this;
    }

    public VagrantProviderConfigBuilder withCustomize(String customize) {
        this.customize.add(customize);
        return this;
    }

    public VagrantProviderConfigBuilder withVmName(String vmName) {
        this.vmName = vmName;
        return this;
    }

    public VagrantProviderConfig build() {
        if (name.isEmpty()) {
            throw new VagrantBuilderException("no name defined");
        }
        return new VagrantProviderConfig(name, guiMode, memory, cpus, customize, vmName);
    }
}
