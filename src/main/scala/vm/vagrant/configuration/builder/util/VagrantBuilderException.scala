package vm.vagrant.configuration.builder.util


/**
  * The default Exception for all builder classes.
  *
  * @author oliver.ziegert
  *
  */
@SerialVersionUID(1L)
class VagrantBuilderException(message: String, cause: Throwable) extends RuntimeException (message, cause){
  def this(message: String) {
    this(message, null)
  }

  def this(cause: Throwable) {
    this(null, cause)
  }
}
