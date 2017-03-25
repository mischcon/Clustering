package vm.vagrant.util

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import scala.io.Source.fromURL

/**
  * Created by oliver.ziegert on 24.03.2017.
  */
object VagrantUtils {
  private var instance: VagrantUtils = _

  def getInstance: VagrantUtils = {
    if (instance == null) instance = new VagrantUtils
    instance
  }
}

class VagrantUtils private() {
  /**
    * Creates a URL for a ressource from classpath.
    *
    * @param path the path to the needed resource
    * @return URL for the resource
    * @throws IOException if the resource is not in the classpath
    */
  @throws[IOException]
  def load(path: String): URL = {
    var url = ClassLoader.getSystemClassLoader.getResource(path)
    if (url == null) { // For use in JAR
      url = this.getClass.getResource(path)
    }
    if (url == null) url = ClassLoader.getSystemResource(path)
    if (url == null) throw new IOException("Can't create URL for path " + path)
    url
  }

  /**
    * Returns a basic Vagrantfile that uses the lucid32 box as template
    *
    * @return the content of the Vagrantfile as String
    */
  def getLucid32MasterContent: String = try {
    val fileUrl = load("com/guigarage/vagrant/master/lucid32")
    fromURL(fileUrl).mkString
  } catch {
    case exception: IOException =>
      throw new RuntimeException(exception)
  }

  /**
    * Returns a basic Vagrantfile that uses the lucid64 box as template
    *
    * @return the content of the Vagrantfile as String
    */
  def getLucid64MasterContent: String = try {
    val fileUrl = load("com/guigarage/vagrant/master/lucid64")
    fromURL(fileUrl).mkString
  } catch {
    case exception: IOException =>
      throw new RuntimeException(exception)
  }

  /**
    * Returns the default URL for the lucid32 box. The box is hosted at vagrantup.com
    *
    * @return The URL for the box
    */
  def getLucid32Url: URL = try new URL("http://files.vagrantup.com/lucid32.box")
  catch {
    case e: MalformedURLException =>
      throw new RuntimeException(e)
  }

  /**
    * Returns the default URL for the lucid64 box. The box is hosted at vagrantup.com
    *
    * @return The URL for the box
    */
  def getLucid64Url: URL = try new URL("http://files.vagrantup.com/lucid64.box")
  catch {
    case e: MalformedURLException =>
      throw new RuntimeException(e)
  }
}