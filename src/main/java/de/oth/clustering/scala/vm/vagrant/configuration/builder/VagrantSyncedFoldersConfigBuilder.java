package de.oth.clustering.scala.vm.vagrant.configuration.builder;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */

public class VagrantSyncedFoldersConfigBuilder implements Serializable {
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





