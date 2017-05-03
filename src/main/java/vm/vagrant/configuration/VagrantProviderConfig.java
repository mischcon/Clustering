package vm.vagrant.configuration;

import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantProviderConfig {
    private String name;
    private Boolean guiMode;
    private Integer memory;
    private Integer cpus;
    private List<String> customize;
    private String vmName;

    public VagrantProviderConfig(String name, Boolean guiMode, Integer memory, Integer cpus, List<String> customize, String vmName) {
        this.name = name;
        this.guiMode = guiMode;
        this.memory = memory;
        this.cpus = cpus;
        this.customize = customize;
        this.vmName = vmName;
    }

    public String name() {
        return name;
    }

    public Boolean guiMode() {
        return guiMode;
    }

    public Integer memory() {
        return memory;
    }

    public Integer cpus() {
        return cpus;
    }

    public List<String> customize() {
        return customize;
    }

    public String vmName() {
        return vmName;
    }
}
