package vm.vagrant.configuration;

import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantSyncedFolderRsyncConfig implements VagrantSyncedFolderConfig {
    private String hostPath;
    private String guestPath;
    private Boolean create;
    private Boolean disabled;
    private String group;
    private List<String> mountOptions;
    private String owner;
    private String name;

    public VagrantSyncedFolderRsyncConfig(String hostPath, String guestPath, Boolean create, Boolean disabled, String group, List<String> mountOptions, String owner, String name) {
        this.hostPath = hostPath;
        this.guestPath = guestPath;
        this.create = create;
        this.disabled = disabled;
        this.group = group;
        this.mountOptions = mountOptions;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public String hostPath() {
        return hostPath;
    }

    @Override
    public String guestPath() {
        return guestPath;
    }

    @Override
    public Boolean create() {
        return create;
    }

    @Override
    public Boolean disabled() {
        return disabled;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public List<String> mountOptions() {
        return mountOptions;
    }

    @Override
    public String owner() {
        return owner;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String mode() {
        return "rsync";
    }
}
