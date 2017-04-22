package vm.vagrant.configuration.builder;

/**
 * Created by oliver.ziegert on 20.04.17.
 */

public class VagrantSyncedFoldersConfigBuilder {
    public static VagrantSyncedFolderNfsConfigBuilder createNfsConfig() {
        return new VagrantSyncedFolderNfsConfigBuilder();
    }

    public static VagrantSyncedFolderVirtualBoxConfigBuilder createVirtualBoxConfig() {
        return new VagrantSyncedFolderVirtualBoxConfigBuilder();
    }

    public static VagrantSyncedFolderRsyncConfigBuilder createRsyncConfig() {
        return new VagrantSyncedFolderRsyncConfigBuilder();
    }
}





