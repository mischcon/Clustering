package vm.vagrant.model

import org.jruby.RubyObject
import org.jruby.RubySymbol
import org.jruby.exceptions.RaiseException
import vm.vagrant.util.VagrantException


/**
  * This class gives you acces to on VM. You can manage the lifecycle of this VM and acces the VM by SSH.
  *
  * @author oliver.ziegert
  *
  */
class VagrantVm(var vagrantVm: RubyObject) {

/**
  * The {@link VagrantVm} is a wrapper for a Vagrant VM. The class contains the JRuby object for the connections and forwards the method calls to it. This constructor is used by the builder classes or the {@link VagrantEnvironment} class. You do not need to call it in your code
  *
  * @param vagrantVm The Vagrant VM connection object
  */
  /**
    * Creates & starts the VM
    */
  def up(): Unit = {
    try
      vagrantVm.callMethod("up")
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Starts the VM
    */
  def start(): Unit = {
    try
      vagrantVm.callMethod("start")
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Halts the VM
    */
  def halt(): Unit = {
    try
      vagrantVm.callMethod("halt")
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Reloades the VM
    */
  def reload(): Unit = {
    try
      vagrantVm.callMethod("reload")
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Destroyes the VM
    */
  def destroy(): Unit = {
    try
      vagrantVm.callMethod("destroy")
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Supsends the VM
    */
  def suspend(): Unit = {
    try
      vagrantVm.callMethod("suspend")
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  def resume(): Unit = {
    try
      vagrantVm.callMethod("resume")
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Returns true if the VM is running
    *
    * @return true if the VM is running
    */
  def isRunning: Boolean = {
    val state = getState
    if (state == "running") return true
    false
  }

  /**
    * Returns true if the VM is created.
    *
    * @return true if the VM is created.
    */
  def isCreated: Boolean = {
    val state = getState
    if (state == "not_created") return false
    true
  }

  /**
    * Returns true if the VM is paused.
    *
    * @return true if the VM is paused.
    */
  def isPaused: Boolean = {
    val state = getState
    if (state == "saved") return true
    false
  }

  /**
    * Returns the state of the VM. Known states are "not_created", "aborted", "poweroff", "running" and "saved"
    *
    * @return the state of the VM
    */
  private def getState = { // not_created, aborted, poweroff, running, saved
    // not_created: VM ist aktuell nicht angelegt
    // aborted: VM wurde (hart) abgebrochen
    // poweroff: VM ist vorhanden aber runtergefahren
    // running: VM läuft
    // saved: VM wurde pausiert
    try {
      vagrantVm.callMethod("state").asInstanceOf[RubySymbol].toString()
    } catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Returns the name of the VM
    *
    * @return the name of the VM
    */
  def getName: String = try
    vagrantVm.callMethod("name").asInstanceOf[RubyObject].toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Returns a new SSH connection to this VM. you can use the connection for upload files or execute command
    *
    * @return a new SSH connection
    */
  def createConnection: VagrantSSHConnection = try
    new VagrantSSHConnection(vagrantVm.callMethod("channel").asInstanceOf[RubyObject])
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Returns the UUID that Vagrant uses internally
    *
    * @return the UUID of this VM
    */
  def getUuid: String = try
    vagrantVm.callMethod("uuid").asInstanceOf[RubyObject].toString
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }
}
