package vm.vagrant.util;

import java.io.Serializable;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public enum ChecksumType implements Serializable {
    MD5("md5"),
    SHA1("sha1"),
    SHA256("sha256");

    private String name;

    ChecksumType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}