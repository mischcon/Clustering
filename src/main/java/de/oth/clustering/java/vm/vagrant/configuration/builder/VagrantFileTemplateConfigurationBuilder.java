package de.oth.clustering.java.vm.vagrant.configuration.builder;

import de.oth.clustering.java.vm.vagrant.configuration.VagrantFileTemplateConfiguration;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class VagrantFileTemplateConfigurationBuilder implements Serializable {

    private File localFile;
    private URL urlTemplate;
    private String pathInVagrantFolder;

    public VagrantFileTemplateConfigurationBuilder() {
    }

    public static VagrantFileTemplateConfigurationBuilder create() {
        return new VagrantFileTemplateConfigurationBuilder();
    }

    public VagrantFileTemplateConfigurationBuilder withUrlTemplate(
            URL urlTemplate) {
        this.urlTemplate = urlTemplate;
        this.localFile = null;
        return this;
    }

    public VagrantFileTemplateConfigurationBuilder withUrlTemplate(
            String urlTemplate) throws MalformedURLException {
        this.urlTemplate = new URL(urlTemplate);
        this.localFile = null;
        return this;
    }

    public VagrantFileTemplateConfigurationBuilder withLocalFile(String localFile) {
        if (localFile == null) {
            this.localFile = null;
        } else {
            this.localFile = new File(localFile);
        }
        this.urlTemplate = null;
        return this;
    }

    public VagrantFileTemplateConfigurationBuilder withLocalFile(File localFile) {
        this.localFile = localFile;
        this.urlTemplate = null;
        return this;
    }

    public VagrantFileTemplateConfigurationBuilder withPathInVagrantFolder(
            String pathInVagrantFolder) {
        this.pathInVagrantFolder = pathInVagrantFolder;
        return this;
    }

    public VagrantFileTemplateConfiguration build() {
        if (localFile != null) {
            return new VagrantFileTemplateConfiguration(localFile,
                    pathInVagrantFolder);
        } else {
            return new VagrantFileTemplateConfiguration(urlTemplate,
                    pathInVagrantFolder);
        }
    }
}
