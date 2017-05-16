package de.oth.clustering.scala.vm.vagrant.util;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public enum Run implements Serializable {
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
