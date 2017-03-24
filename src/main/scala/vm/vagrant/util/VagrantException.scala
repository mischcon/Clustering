package vm.vagrant.util


/**
  * Default Exception for the vagrant-binding. Any Exception in Ruby / Vagrant is wrapped in a VagrantException
  *
  * @author oliver.ziegert
  *
  */
@SerialVersionUID(1L)
class VagrantException(message: String, cause: Throwable) extends RuntimeException (message, cause) {
  def this(message: String) {
    this(message, null)
  }

  def this(cause: Throwable) {
    this(null, cause)
  }
}
