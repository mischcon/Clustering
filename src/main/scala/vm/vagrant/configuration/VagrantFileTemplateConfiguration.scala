package vm.vagrant.configuration

import java.io.File
import java.net.URL

/**
  * Created by oliver.ziegert on 24.03.17.
  */
/**
  * A configuration for a Vagrant file template. The local file defined by this
  * template will copied into the folder of the Vagrant environment the VM is
  * running in.
  *
  * @author oliver.ziegert
  *
  */
class VagrantFileTemplateConfiguration (){
  private var localFile: File = _
  private var urlTemplate: URL = _
  private var pathInVagrantFolder: String = _

  /**
    * Creates a new {@link VagrantFileTemplateConfiguration} that uses a URL for the template file
    *
    * @param urlTemplate         url of the template
    * @param pathInVagrantFolder path in Vagrant folder where the template should be copied
    */
  def this(urlTemplate: URL, pathInVagrantFolder: String) {
    this()
    this.urlTemplate = urlTemplate
    this.pathInVagrantFolder = pathInVagrantFolder
  }

  /**
    * Creates a new {@link VagrantFileTemplateConfiguration} that uses a local path for the template file
    *
    * @param localFile           locale path of the template
    * @param pathInVagrantFolder path in Vagrant folder where the template should be copied
    */
  def this(localFile: File, pathInVagrantFolder: String) {
    this()
    this.localFile = localFile
    this.pathInVagrantFolder = pathInVagrantFolder
  }

  /**
    * You can use a locale path or a URL to define the local file. So you can
    * also use any data / file from the internet or the classpath. This returns
    * true if a URL is used for the local file.
    *
    * @return true if a URL is used for the local file
    */
  def useUrlTemplate: Boolean = {
    if (urlTemplate != null) return true
    false
  }

  /**
    * You can use a locale path or a URL to define the local file. So you can
    * also use any data / file from the internet or the classpath. This returns
    * true if a path is used for the local file.
    *
    * @return true if a path is used for the local file
    */
  def useLocalFile: Boolean = {
    if (localFile != null) return true
    false
  }

  /**
    * Returns the URL of the template file
    *
    * @return the URL of the template file
    */
  def getUrlTemplate: URL = urlTemplate

  /**
    * Returns the locale path of the template file
    *
    * @return the locale path of the template file
    */
  def getLocalFile: File = localFile

  /**
    * Returns the path inside the Vagrant folder where the template should be copied to.
    *
    * @return the path inside the Vagrant folder
    */
  def getPathInVagrantFolder: String = pathInVagrantFolder
}

