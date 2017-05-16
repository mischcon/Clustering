package de.oth.clustering.scala.vm.vagrant.configuration.builder;

import de.oth.clustering.scala.vm.vagrant.configuration.VagrantProvisionerConfig;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface IVagrantProvisionerConfigBuilder extends Serializable{
    VagrantProvisionerConfig build();
}
