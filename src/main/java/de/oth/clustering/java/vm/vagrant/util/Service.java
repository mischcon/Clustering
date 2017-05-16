package de.oth.clustering.java.vm.vagrant.util;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public enum Service implements Serializable {
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
