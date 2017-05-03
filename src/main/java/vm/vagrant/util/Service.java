package vm.vagrant.util;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public enum Service {
    HTTP("http"),
    HTTPS("https"),
    SSH("ssh"),
    MYSQL("mysql");

    private String name;

    Service(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
