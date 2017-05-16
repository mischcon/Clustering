package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantSyncedFolderConfig;
import de.oth.clustering.java.vm.vagrant.configuration.VagrantSyncedFolderNfsConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantSyncedFolderNfsConfigBuilder implements IVagrantSyncedFoldersConfigBuilder {
    private String hostPath;
    private String guestPath;
    private Boolean create;
    private Boolean disabled;
    private String group;
    private List<String> mountOptions;
    private String owner;
    private String name;
    private Boolean nfsExport;
    private Boolean nfsUdp;
    private Integer nfsVersion;

    public VagrantSyncedFolderNfsConfigBuilder() {
        mountOptions = new ArrayList<String>();
        create = false;
        disabled = false;
        nfsExport = true;
        nfsUdp = true;
        nfsVersion = 3;
    }

    public VagrantSyncedFolderNfsConfigBuilder withHostPath(String hostPath) {
        this.hostPath = hostPath;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withGuestPath(String guestPath) {
        this.guestPath = guestPath;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withCreate(Boolean create) {
        this.create = create;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withGroup(String group) {
        this.group = group;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withMountOption(String mountOption) {
        this.mountOptions.add(mountOption);
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withNfsExport(Boolean nfsExport) {
        this.nfsExport = nfsExport;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withNfsUdp(Boolean nfsUdp) {
        this.nfsUdp = nfsUdp;
        return this;
    }

    public VagrantSyncedFolderNfsConfigBuilder withNfsVersion(Integer nfsVersion) {
        this.nfsVersion = nfsVersion;
        return this;
    }

    @Override
    public VagrantSyncedFolderConfig build() {
        return new VagrantSyncedFolderNfsConfig(hostPath, guestPath, create, disabled, group, mountOptions, owner, name, nfsExport, nfsUdp, nfsVersion);
    }
}
