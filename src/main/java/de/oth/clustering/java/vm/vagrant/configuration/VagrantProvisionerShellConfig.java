package de.oth.clustering.java.vm.vagrant.configuration;

import de.oth.clustering.java.vm.vagrant.util.Run;

import java.io.File;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */
public class VagrantProvisionerShellConfig implements VagrantProvisionerConfig {
    private Run run;
    private Boolean preserveOrder;
    private String inline;
    private File path;
    private List<String> args;
    private Boolean binary;
    private Boolean privileged;
    private File uploadPath;
    private Boolean keepColor;
    private String name;
    private String md5;
    private String sha1;

    public VagrantProvisionerShellConfig(Run run, Boolean preserveOrder, String inline, File path, List<String> args, Boolean binary, Boolean privileged, File uploadPath, Boolean keepColor, String name, String md5, String sha1) {
        this.run = run;
        this.preserveOrder = preserveOrder;
        this.inline = inline;
        this.path = path;
        this.args = args;
        this.binary = binary;
        this.privileged = privileged;
        this.uploadPath = uploadPath;
        this.keepColor = keepColor;
        this.name = name;
        this.md5 = md5;
        this.sha1 = sha1;
    }

    @Override
    public Run run() {
        return run;
    }

    @Override
    public Boolean preserveOrder() {
        return preserveOrder;
    }

    public String inline() {
        return inline;
    }

    public File path() {
        return path;
    }

    public List<String> args() {
        return args;
    }

    public Boolean binary() {
        return binary;
    }

    public Boolean privileged() {
        return privileged;
    }

    public File uploadPath() {
        return uploadPath;
    }

    public Boolean keepColor() {
        return keepColor;
    }

    public String name() {
        return name;
    }

    public String md5() {
        return md5;
    }

    public String sha1() {
        return sha1;
    }

    @Override
    public String mode() {
        return "shell";
    }
}
