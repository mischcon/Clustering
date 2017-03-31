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
  def up(): String = {
    try
      vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "up")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def destroy(): String = {
    try
      vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "destroy"), RubyString.newString(vagrantEnvironment.getRuntime, "-f")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Creates a simple vagrantfile / configuration for this environment. The configuration contains only one VM that uses the given box
    *
    * @param boxName name of the box for the VM
    */
  def init(boxName: String): String = {
    try
      vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "init"), RubyString.newString(vagrantEnvironment.getRuntime, boxName)).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }


  /**
    * Creates a iterator for all available boxes in Vagrant.
    *
    * @return a iterator for all boxes.
    */
  def getAllAvailableBoxes: Iterable[String] = try {
    val boxes = vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "box"), RubyString.newString(vagrantEnvironment.getRuntime, "list")).convertToString().toString
    boxes.split("\n").map(d => d.split(" "){0})
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

}
