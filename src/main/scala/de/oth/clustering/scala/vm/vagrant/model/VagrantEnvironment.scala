package de.oth.clustering.scala.vm.vagrant.model

import org.jruby.exceptions.RaiseException
import org.jruby.{RubyObject, RubyString}
import de.oth.clustering.scala.vm.vagrant.configuration.VagrantVmConfig
import de.oth.clustering.scala.vm.vagrant.util.{VagrantException, VagrantVmConfigUtils}

/**
  * A {@link VmEnvironment} manages a set of VMs. By using the environment you can manage the lifecycle of all VMs inside the environment or access a specific VM.
  *
  * @author oliver.ziegert
  *
  */
class VagrantEnvironment(var vagrantEnvironment: RubyObject) {

/**
  * The {@link VmEnvironment} is a Wrapper for a Vagrant environment. The class contains the JRuby object for the connections and forwards the method calls to it. This constructor is used by the builder classes or the {@link Vagrant} class. You do not need to call it in your code
  */
  /**
    * Start all VMs in this environment
    */
  def up(vmConfig: VagrantVmConfig = null): String = {
    try
      if (vmConfig != null)
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "up"), RubyString.newString(vagrantEnvironment.getRuntime, vmConfig.name)).convertToString().toString
      else
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "up")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def destroy(vmConfig: VagrantVmConfig = null): String = {
    try
      if (vmConfig != null)
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "destroy"), RubyString.newString(vagrantEnvironment.getRuntime, "-f"), RubyString.newString(vagrantEnvironment.getRuntime, vmConfig.name)).convertToString().toString
      else
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "destroy"), RubyString.newString(vagrantEnvironment.getRuntime, "-f")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def halt(vmConfig: VagrantVmConfig = null): String = {
    try
      if (vmConfig != null)
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "halt"), RubyString.newString(vagrantEnvironment.getRuntime, vmConfig.name)).convertToString().toString
      else
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "halt")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def resume(vmConfig: VagrantVmConfig = null): String = {
    try
      if (vmConfig != null)
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "resume"), RubyString.newString(vagrantEnvironment.getRuntime, vmConfig.name)).convertToString().toString
      else
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "resume")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def reload(vmConfig: VagrantVmConfig = null): String = {
    try
      if (vmConfig != null)
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "reload"), RubyString.newString(vagrantEnvironment.getRuntime, vmConfig.name)).convertToString().toString
      else
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "reload")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def suspend(vmConfig: VagrantVmConfig = null): String = {
    try
        if (vmConfig != null)
          vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "suspend"), RubyString.newString(vagrantEnvironment.getRuntime, vmConfig.name)).convertToString().toString
        else
          vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "suspend")).convertToString().toString
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def status(vmConfig: VagrantVmConfig = null): Iterator[(String, VmStatus.Value)] = {
    try {
      var output = ""
      if (vmConfig != null)
        output = vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "status"), RubyString.newString(vagrantEnvironment.getRuntime, vmConfig.name)).convertToString().toString
      else
        output = vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "status")).convertToString().toString
      val pattern = s".*\\s(${VmStatus.notCreated.toString}|${VmStatus.poweroff.toString}|${VmStatus.running.toString})\\s\\(.*\\)".r
      val regex = pattern.findAllMatchIn(output)
      regex.map(d => {
        val status = d.toString()
        if (status.contains(VmStatus.running.toString)) (status.split("\\s"){0}, VmStatus.running)
        else if (status.contains(VmStatus.poweroff.toString)) (status.split("\\s"){0}, VmStatus.running)
        else if (status.contains(VmStatus.notCreated.toString)) (status.split("\\s"){0}, VmStatus.notCreated)
      }).collect[(String, VmStatus.Value)]({case x: (String, VmStatus.Value) => x})
    } catch {
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

  def addBoxe(boxName: String): String = try
    vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "box"), RubyString.newString(vagrantEnvironment.getRuntime, "add"), RubyString.newString(vagrantEnvironment.getRuntime, boxName)).convertToString().toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def removeBoxes(boxName: String): String = try
    vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "box"), RubyString.newString(vagrantEnvironment.getRuntime, "remove"), RubyString.newString(vagrantEnvironment.getRuntime, boxName)).convertToString().toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def updateBoxes(boxName: String = null): String = try {
    if (boxName == null) {
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "box"), RubyString.newString(vagrantEnvironment.getRuntime, "update")).convertToString().toString
      } else {
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "box"), RubyString.newString(vagrantEnvironment.getRuntime, "update"), RubyString.newString(vagrantEnvironment.getRuntime, "--box"), RubyString.newString(vagrantEnvironment.getRuntime, boxName)).convertToString().toString
    }
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def getBoxePortMapping(box: VagrantVmConfig): VagrantVmConfig = try {
    val output = vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "port"), RubyString.newString(vagrantEnvironment.getRuntime, box.name)).convertToString().toString
    val pattern = s"\\d+ \\(guest\\) => \\d+ \\(host\\)".r
    val regex = pattern.findAllMatchIn(output)
    val mapping = regex.map(d => {val map = d.toString().split(" "); (map{0}.toInt, map{3}.toInt)}).collect({case x:(Int, Int) => x})
    VagrantVmConfigUtils.updatePortMapping(box, mapping)
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def provision(box: VagrantVmConfig = null): String = try
    if (box == null)
      vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "provision")).convertToString().toString
    else
      vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "provision"), RubyString.newString(vagrantEnvironment.getRuntime, box.name)).convertToString().toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def snapshotPush(box: VagrantVmConfig = null): String = try
    if (box == null)
      vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "snapshot"), RubyString.newString(vagrantEnvironment.getRuntime, "push")).convertToString().toString
    else
      vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "snapshot"), RubyString.newString(vagrantEnvironment.getRuntime, "push"), RubyString.newString(vagrantEnvironment.getRuntime, box.name)).convertToString().toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def snapshotPop(box: VagrantVmConfig = null): String = try
      if (box == null)
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "snapshot"), RubyString.newString(vagrantEnvironment.getRuntime, "pop"), RubyString.newString(vagrantEnvironment.getRuntime, "--no-delete")).convertToString().toString
      else
        vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "snapshot"), RubyString.newString(vagrantEnvironment.getRuntime, "pop"), RubyString.newString(vagrantEnvironment.getRuntime, "--no-delete"), RubyString.newString(vagrantEnvironment.getRuntime, box.name)).convertToString().toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def sshExecute(box: VagrantVmConfig, command: String): String = try {
    var output = vagrantEnvironment.callMethod("get_output", RubyString.newString(vagrantEnvironment.getRuntime, "ssh"), RubyString.newString(vagrantEnvironment.getRuntime, "-c"), RubyString.newString(vagrantEnvironment.getRuntime, s""""$command""""), RubyString.newString(vagrantEnvironment.getRuntime, box.name)).convertToString().toString
    output.substring(0, output.lastIndexOf('\n'))
  } catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

}

object VmStatus extends Enumeration {
  type VmStatus = Value
  val notCreated = Value("not created")
  val poweroff = Value("poweroff")
  val running = Value("running")
}
