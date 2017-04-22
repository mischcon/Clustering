package vm.vagrant.util;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public enum Run {
    ALWAYS("always"),
    NEVER("never"),
    ONCE("once");

    private String name;

    Run(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
