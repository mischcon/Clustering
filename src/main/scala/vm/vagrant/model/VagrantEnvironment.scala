package vm.vagrant.model

import java.net.URL
import java.util
import org.jruby.RubyArray
import org.jruby.RubyBoolean
import org.jruby.RubyNil
import org.jruby.RubyObject
import org.jruby.RubyString
import org.jruby.exceptions.RaiseException
import vm.vagrant.util.VagrantException
import scala.collection.JavaConversions._

/**
  * A {@link VagrantEnvironment} manages a set of VMs. By using the environment you can manage the lifecycle of all VMs inside the environment or access a specific VM.
  *
  * @author oliver.ziegert
  *
  */
class VagrantEnvironment(var vagrantEnvironment: RubyObject) {

/**
  * The {@link VagrantEnvironment} is a Wrapper for a Vagrant environment. The class contains the JRuby object for the connections and forwards the method calls to it. This constructor is used by the builder classes or the {@link Vagrant} class. You do not need to call it in your code
  */
  /**
    * Start all VMs in this environment
    */
  def up() {
    try
      vagrantEnvironment.callMethod("execute", RubyString.newString(vagrantEnvironment.getRuntime, "up"))
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def destroy() {
    try
      vagrantEnvironment.callMethod("execute", RubyString.newString(vagrantEnvironment.getRuntime, "destroy"), RubyString.newString(vagrantEnvironment.getRuntime, "-f"))
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }


  /**
    * Adds a new box to Vagrant
    *
    * @param boxName name of the new box
    * @param boxUrl  the url of the template box. For example "http://files.vagrantup.com/lucid32.box"
    */
  def addBox(boxName: String, boxUrl: URL): Unit = {
    try
      vagrantEnvironment.callMethod("execute", RubyString.newString(vagrantEnvironment.getRuntime, "add"), RubyString.newString(vagrantEnvironment.getRuntime, boxName), RubyString.newString(vagrantEnvironment.getRuntime, boxUrl.toString))
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Removes a box from Vagrant
    *
    * @param boxName name of the box you want to remove
    */
  def removeBox(boxName: String): Unit = {
    try {
      val boxes = vagrantEnvironment.callMethod("boxes").asInstanceOf[RubyObject].getInternalVariable("@boxes").asInstanceOf[RubyArray]
      import scala.collection.JavaConversions._
      for (box <- boxes) {
        val name = box.asInstanceOf[RubyObject].callMethod("name").toString
        if (name == boxName) box.asInstanceOf[RubyObject].callMethod("destroy")
      }
    } catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Returns the main path to all box templates that Vagrant has installed on your system.
    *
    * @return
    */
  def getBoxesPath: String = try
    vagrantEnvironment.callMethod("boxes_path").asInstanceOf[RubyObject].toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Creates a simple vagrantfile / configuration for this environment. The configuration contains only one VM that uses the given box
    *
    * @param boxName name of the box for the VM
    */
  def init(boxName: String): Unit = {
    try
      vagrantEnvironment.callMethod("execute", RubyString.newString(vagrantEnvironment.getRuntime, "init"), RubyString.newString(vagrantEnvironment.getRuntime, boxName))
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Return true if more than one VM is configured in this environment
    *
    * @return true if more than one VM is configured in this environment
    */
  def isMultiVmEnvironment: Boolean = try
    vagrantEnvironment.callMethod("multivm?").asInstanceOf[RubyBoolean].isTrue
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Each Vagrant environment is configured in a path on your system.
    *
    * @return path for this environment
    */
  def getRootPath: String = try
    vagrantEnvironment.callMethod("root_path").asInstanceOf[RubyObject].toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Creates a iterator for all available boxes in Vagrant.
    *
    * @return a iterator for all boxes.
    */
  def getAllAvailableBoxes: Iterable[String] = try {
    val boxes = vagrantEnvironment.callMethod("boxes").asInstanceOf[RubyObject].getInternalVariable("@boxes").asInstanceOf[RubyArray]
    val ret = new util.ArrayList[String]
    for (box <- boxes) {
      ret.add(box.asInstanceOf[RubyObject].callMethod("name").toString)
    }
    ret
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Creates a iterator for all configured VMs in this environment.
    *
    * @return a iterator for all VMs in this environment.
    */
  def getAllVms: Iterable[VagrantVm] = try {
    val o = vagrantEnvironment.callMethod("vms_ordered").asInstanceOf[RubyArray]
    val vms = new util.ArrayList[VagrantVm]
    for (vm <- o) {
      vms.add(new VagrantVm(vm.asInstanceOf[RubyObject]))
    }
    vms
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Returns a specific VM at the given index.
    *
    * @param index the index
    * @return the VM at the given index
    */
  def getVm(index: Int): VagrantVm = try {
    val o = vagrantEnvironment.callMethod("vms_ordered").asInstanceOf[RubyArray]
    new VagrantVm(o.get(index).asInstanceOf[RubyObject])
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Returns the count of all VMs configured in this environment
    *
    * @return the count of all VMs
    */
  def getVmCount: Int = try {
    val o = vagrantEnvironment.callMethod("vms_ordered").asInstanceOf[RubyArray]
    o.size
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Returns the filename of the Vagrantfile for this environment. Normally the name is "Vagrantfile"
    *
    * @return the filename of the Vagrantfile
    */
  def getVagrantfileName: String = try
    vagrantEnvironment.callMethod("vagrantfile_name").asInstanceOf[RubyObject].toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Returns the global home path for Vagrant. This path is used by Vagrant to store global configs and states
    *
    * @return the global home path for Vagrant
    */
  def getHomePath: String = try
    vagrantEnvironment.callMethod("home_path").asInstanceOf[RubyObject].toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * If this environment is a single VM environment (only contains one VM) this methode will return the VM object.
    *
    * @return the object for the VM in this environment.
    */
  def getPrimaryVm: VagrantVm = try {
    val rubyVm = vagrantEnvironment.callMethod("primary_vm").asInstanceOf[RubyObject]
    if (rubyVm == null || rubyVm.isInstanceOf[RubyNil]) throw new VagrantException("No primary vm found. Maybe there is no vm defined in your configuration or you are working with a multi vm environment.")
    new VagrantVm(rubyVm)
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

}
