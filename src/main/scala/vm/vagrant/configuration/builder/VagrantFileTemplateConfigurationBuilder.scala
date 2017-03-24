package vm.vagrant.configuration.builder

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.MalformedURLException
import java.net.URL
import vm.vagrant.configuration.VagrantFileTemplateConfiguration


object VagrantFileTemplateConfigurationBuilder {
  def create = new VagrantFileTemplateConfigurationBuilder
}

class VagrantFileTemplateConfigurationBuilder() {
  private var localFile: File = _
  private var urlTemplate: URL = _
  private var pathInVagrantFolder: String = _

  def withUrlTemplate(urlTemplate: URL): VagrantFileTemplateConfigurationBuilder = {
    this.urlTemplate = urlTemplate
    this.localFile = null
    this
  }

  @throws[MalformedURLException]
  def withUrlTemplate(urlTemplate: String): VagrantFileTemplateConfigurationBuilder = {
    this.urlTemplate = new URL(urlTemplate)
    this.localFile = null
    this
  }

  def withLocalFile(localFile: String): VagrantFileTemplateConfigurationBuilder = {
    if (localFile == null) this.localFile = null
    else this.localFile = new File(localFile)
    this.urlTemplate = null
    this
  }

  def withLocalFile(localFile: File): VagrantFileTemplateConfigurationBuilder = {
    this.localFile = localFile
    this.urlTemplate = null
    this
  }

  def withPathInVagrantFolder(pathInVagrantFolder: String): VagrantFileTemplateConfigurationBuilder = {
    this.pathInVagrantFolder = pathInVagrantFolder
    this
  }

  def build: VagrantFileTemplateConfiguration = if (localFile != null) new VagrantFileTemplateConfiguration(localFile, pathInVagrantFolder)
  else new VagrantFileTemplateConfiguration(urlTemplate, pathInVagrantFolder)
}
