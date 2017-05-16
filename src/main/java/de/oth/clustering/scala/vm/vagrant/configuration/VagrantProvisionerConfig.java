package de.oth.clustering.scala.vm.vagrant.configuration;

import de.oth.clustering.scala.vm.vagrant.util.Run;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface VagrantProvisionerConfig extends Serializable {
    String mode();
    Run run();
    Boolean preserveOrder();
}