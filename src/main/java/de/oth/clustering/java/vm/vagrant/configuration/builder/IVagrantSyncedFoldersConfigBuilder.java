package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantSyncedFolderConfig;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface IVagrantSyncedFoldersConfigBuilder extends Serializable{
    VagrantSyncedFolderConfig build();
}
