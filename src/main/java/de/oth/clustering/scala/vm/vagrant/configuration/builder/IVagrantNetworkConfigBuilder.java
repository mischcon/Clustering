package de.oth.clustering.scala.vm.vagrant.configuration.builder;

import de.oth.clustering.scala.vm.vagrant.configuration.VagrantNetworkConfig;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface IVagrantNetworkConfigBuilder extends Serializable{
    VagrantNetworkConfig build();
}
