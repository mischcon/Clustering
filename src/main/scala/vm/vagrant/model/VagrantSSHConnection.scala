package vm.vagrant.model

import org.jruby.RubyBoolean
import org.jruby.RubyNumeric
import org.jruby.RubyObject
import org.jruby.RubyString
import org.jruby.exceptions.RaiseException
import vm.vagrant.util.VagrantException


/**
  * Wrapper for a Vagrant SSH connection. The class contains the JRuby object for the connections and forwards the method calls to it.
  * You can execute commands on the VM or upload files to it.
  *
  * @author oliver.ziegert
  *
  */
class VagrantSSHConnection(vagrantSSH: RubyObject)

/**
  * Constructor for the SHH connection. Normally you do not need to create a connection on your own. Use {@link VagrantVm.createConnection()} to create a new SSH connection.
  *
  * @param vagrantSSH The Vagrant SSH connection object
  */ {
  /**
    * Checks if the connection is ready. Normally you do not need this this methode because the connection should be always ready.
    *
    * @return true if the SSH connection is ready
    */
  def isReady: Boolean = try
    vagrantSSH.callMethod("ready?").asInstanceOf[RubyBoolean].isTrue
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  /**
    * Uploads a file to the vm.
    *
    * @param localPath the locale path of the file
    * @param pathOnVM  the path on the vm where you want to upload the local file
    */
  def upload(localPath: String, pathOnVM: String): Unit = {
    try
      vagrantSSH.callMethod("upload", RubyString.newString(vagrantSSH.getRuntime, localPath), RubyString.newString(vagrantSSH.getRuntime, pathOnVM))
    catch {
      case exception: RaiseException =>
        throw new VagrantException(exception)
    }
  }

  /**
    * Executes a command on the vm
    *
    * @param command the command you want to execute. for example "touch /file.tmp"
    * @param sudo    if true the command will be executed as sudo
    * @return the returncode of the command
    */
  def execute(command: String, sudo: Boolean): Int = try
      if (sudo) {
        val number = vagrantSSH.callMethod("sudo", RubyString.newString(vagrantSSH.getRuntime, command)).asInstanceOf[RubyNumeric]
        number.getLongValue.toInt
      }
      else {
        val number = vagrantSSH.callMethod("execute", RubyString.newString(vagrantSSH.getRuntime, command)).asInstanceOf[RubyNumeric]
        number.getLongValue.toInt
      }
  catch {
    case exception: RaiseException =>
      throw new VagrantException(exception)
  }

  def execute(command: String): Int = execute(command, false)
}
