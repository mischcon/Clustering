package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantSyncedFolderVirtualBoxConfig;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantSyncedFolderConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantSyncedFolderVirtualBoxConfigBuilder implements IVagrantSyncedFoldersConfigBuilder {
    private String hostPath;
    private String guestPath;
    private Boolean create;
    private Boolean disabled;
    private String group;
    private List<String> mountOptions;
    private String owner;
    private String name;

    public VagrantSyncedFolderVirtualBoxConfigBuilder() {
        mountOptions = new ArrayList<String>();
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withHostPath(String hostPath) {
        this.hostPath = hostPath;
        return this;
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withGuestPath(String guestPath) {
        this.guestPath = guestPath;
        return this;
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withCreate(Boolean create) {
        this.create = create;
        return this;
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withGroup(String group) {
        this.group = group;
        return this;
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withMountOption(String mountOption) {
        this.mountOptions.add(mountOption);
        return this;
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public VagrantSyncedFolderVirtualBoxConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public VagrantSyncedFolderConfig build() {
        return new VagrantSyncedFolderVirtualBoxConfig(hostPath, guestPath, create, disabled, group, mountOptions, owner, name);
    }
}
