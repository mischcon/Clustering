package de.oth.clustering.scala.vm.vagrant.configuration.builder;

import de.oth.clustering.scala.vm.vagrant.configuration.VagrantFolderTemplateConfiguration;
import de.oth.clustering.scala.vm.vagrant.configuration.builder.util.VagrantBuilderException;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

public class VagrantFolderTemplateConfigurationBuilder implements Serializable {

	private File localFolder;
	private String pathInVagrantFolder;
	private URI uriTemplate;

	public VagrantFolderTemplateConfigurationBuilder() {
	}

	public static VagrantFolderTemplateConfigurationBuilder create() {
		return new VagrantFolderTemplateConfigurationBuilder();
	}

	public VagrantFolderTemplateConfigurationBuilder withUrlTemplate(
			URI uriTemplate) {
		this.uriTemplate = uriTemplate;
		this.localFolder = null;
		return this;
	}
	
	public VagrantFolderTemplateConfigurationBuilder withLocalFolder(
			String localFolder) {
		if(localFolder == null) {
			this.localFolder = null;
		} else {
			this.localFolder = new File(localFolder);
		}
		return this;
	}
	
	public VagrantFolderTemplateConfigurationBuilder withLocalFolder(
			File localFolder) {
		this.localFolder = localFolder;
		this.uriTemplate = null;
		return this;
	}

	public VagrantFolderTemplateConfigurationBuilder withPathInVagrantFolder(
			String pathInVagrantFolder) {
		this.pathInVagrantFolder = pathInVagrantFolder;
		return this;
	}

	public VagrantFolderTemplateConfiguration build() {
		if(localFolder == null && uriTemplate == null) {
			throw new VagrantBuilderException("localFolder or uriTemplate need to be specified");
		}
		if(pathInVagrantFolder == null) {
			throw new VagrantBuilderException("pathInVagrantFolder need to be specified");
		}
		if (localFolder != null) {
			return new VagrantFolderTemplateConfiguration(localFolder,
					pathInVagrantFolder);
		} else {
			return new VagrantFolderTemplateConfiguration(uriTemplate,
					pathInVagrantFolder);
		}
	}
}