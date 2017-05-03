package vm.vagrant.configuration;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public interface VagrantSyncedFolderConfig extends Serializable{
    String hostPath();
    String guestPath();
    String mode();
    Boolean create();
    Boolean disabled();
    String group();
    List<String> mountOptions();
    String owner();
    String name();
}
