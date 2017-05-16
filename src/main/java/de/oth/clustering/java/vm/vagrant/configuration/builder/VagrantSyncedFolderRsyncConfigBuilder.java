package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantSyncedFolderConfig;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantSyncedFolderRsyncConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantSyncedFolderRsyncConfigBuilder implements IVagrantSyncedFoldersConfigBuilder {
    private String hostPath;
    private String guestPath;
    private Boolean create;
    private Boolean disabled;
    private String group;
    private List<String> mountOptions;
    private String owner;
    private String name;

    public VagrantSyncedFolderRsyncConfigBuilder() {
        mountOptions = new ArrayList<String>();
    }

    public VagrantSyncedFolderRsyncConfigBuilder withHostPath(String hostPath) {
        this.hostPath = hostPath;
        return this;
    }

    public VagrantSyncedFolderRsyncConfigBuilder withGuestPath(String guestPath) {
        this.guestPath = guestPath;
        return this;
    }

    public VagrantSyncedFolderRsyncConfigBuilder withCreate(Boolean create) {
        this.create = create;
        return this;
    }

    public VagrantSyncedFolderRsyncConfigBuilder withDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public VagrantSyncedFolderRsyncConfigBuilder withGroup(String group) {
        this.group = group;
        return this;
    }

    public VagrantSyncedFolderRsyncConfigBuilder withMountOption(String mountOption) {
        this.mountOptions.add(mountOption);
        return this;
    }

    public VagrantSyncedFolderRsyncConfigBuilder withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public VagrantSyncedFolderRsyncConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public VagrantSyncedFolderConfig build() {
        return new VagrantSyncedFolderRsyncConfig(hostPath, guestPath, create, disabled, group, mountOptions, owner, name);
    }
}