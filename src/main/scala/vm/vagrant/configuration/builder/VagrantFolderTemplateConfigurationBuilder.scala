package vm.vagrant.configuration.builder

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.URI
import vm.vagrant.configuration.VagrantFolderTemplateConfiguration
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantFolderTemplateConfigurationBuilder {
  def create = new VagrantFolderTemplateConfigurationBuilder
}

class VagrantFolderTemplateConfigurationBuilder() {
  private var localFolder: File = _
  private var pathInVagrantFolder: String = _
  private var uriTemplate: URI = _

  def withUrlTemplate(uriTemplate: URI): VagrantFolderTemplateConfigurationBuilder = {
    this.uriTemplate = uriTemplate
    this.localFolder = null
    this
  }

  def withLocalFolder(localFolder: String): VagrantFolderTemplateConfigurationBuilder = {
    if (localFolder == null) this.localFolder = null
    else this.localFolder = new File(localFolder)
    this
  }

  def withLocalFolder(localFolder: File): VagrantFolderTemplateConfigurationBuilder = {
    this.localFolder = localFolder
    this.uriTemplate = null
    this
  }

  def withPathInVagrantFolder(pathInVagrantFolder: String): VagrantFolderTemplateConfigurationBuilder = {
    this.pathInVagrantFolder = pathInVagrantFolder
    this
  }

  def build: VagrantFolderTemplateConfiguration = {
    if (localFolder == null && uriTemplate == null) throw new VagrantBuilderException("localFolder or uriTemplate need to be specified")
    if (pathInVagrantFolder == null) throw new VagrantBuilderException("pathInVagrantFolder need to be specified")
    if (localFolder != null) new VagrantFolderTemplateConfiguration(localFolder, pathInVagrantFolder)
    else new VagrantFolderTemplateConfiguration(uriTemplate, pathInVagrantFolder)
  }
}