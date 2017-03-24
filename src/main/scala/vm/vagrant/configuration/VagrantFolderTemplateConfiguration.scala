package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.URI


class VagrantFolderTemplateConfiguration {
  private var localFolder: File = _
  private var pathInVagrantFolder: String = _
  private var uriTemplate: URI = _

  def this(uriTemplate: URI, pathInVagrantFolder: String) {
    this()
    this.uriTemplate = uriTemplate
    this.pathInVagrantFolder = pathInVagrantFolder
  }

  /**
    * Creates a new {@link VagrantFolderTemplateConfiguration} that uses a local path for the template folder
    *
    * @param localFolder           locale path of the template folder
    * @param pathInVagrantFolder path in Vagrant folder where the template should be copied
    */
  def this(localFolder: File, pathInVagrantFolder: String) {
    this()
    this.localFolder = localFolder
    this.pathInVagrantFolder = pathInVagrantFolder
  }

  def useUriTemplate: Boolean = {
    if (uriTemplate != null) return true
    false
  }

  def getLocalFolder: File = localFolder

  /**
    * Returns the path inside the Vagrant folder where the template should be copied to.
    *
    * @return the path inside the Vagrant folder
    */
  def getPathInVagrantFolder: String = pathInVagrantFolder

  def getUriTemplate: URI = uriTemplate
}
