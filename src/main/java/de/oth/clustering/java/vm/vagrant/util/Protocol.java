package de.oth.clustering.java.vm.vagrant.util;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public enum Protocol implements Serializable {
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
