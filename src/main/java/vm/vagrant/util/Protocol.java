package vm.vagrant.util;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public enum Protocol {
    UDP("udp"),
    TCP("tcp");

    private String name;

    Protocol(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
