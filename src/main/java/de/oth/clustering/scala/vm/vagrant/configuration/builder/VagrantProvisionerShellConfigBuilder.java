package de.oth.clustering.scala.vm.vagrant.configuration.builder;

import de.oth.clustering.scala.vm.vagrant.configuration.VagrantProvisionerConfig;
import de.oth.clustering.scala.vm.vagrant.configuration.VagrantProvisionerShellConfig;
import de.oth.clustering.scala.vm.vagrant.util.Run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver.ziegert on 20.04.17.
 */

public class VagrantProvisionerShellConfigBuilder implements IVagrantProvisionerConfigBuilder {
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

    public VagrantProvisionerShellConfigBuilder() {
        run = Run.ONCE;
        preserveOrder = false;
        args = new ArrayList<String>();
        privileged = true;
    }

    public VagrantProvisionerShellConfigBuilder withRun(Run run) {
        this.run = run;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withPreserveOrder(Boolean preserveOrder) {
        this.preserveOrder = preserveOrder;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withInline(String inline) {
        this.inline = inline;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withPath(File path) {
        this.path = path;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withArg(String arg) {
        this.args.add(arg);
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withBinary(Boolean binary) {
        this.binary = binary;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withPrivileged(Boolean privileged) {
        this.privileged = privileged;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withUploadPath(File uploadPath) {
        this.uploadPath = uploadPath;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withKeepColor(Boolean keepColor) {
        this.keepColor = keepColor;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public VagrantProvisionerShellConfigBuilder withSha1(String sha1) {
        this.sha1 = sha1;
        return this;
    }

    @Override
    public VagrantProvisionerConfig build() {
        return new VagrantProvisionerShellConfig(run, preserveOrder, inline, path, args, binary, privileged, uploadPath, keepColor, name, md5, sha1);
    }
}