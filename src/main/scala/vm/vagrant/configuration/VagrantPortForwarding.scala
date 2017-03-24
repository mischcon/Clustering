package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */
/**
  * This class configures a port forwarding for one Vagrant VM
  *
  * @author oliver.ziegert
  *
  */
class VagrantPortForwarding(var name: String, var guestport: Int, var hostport: Int) {

/**
  * Constructor for the port forwarding
  */
  /**
    * The name of the port forwarding. This is optional and used by Vagrant internally
    *
    * @return name of the port forwarding
    */
  def getName: String = name

  /**
    * Returns the guestport.
    *
    * @return the guestport
    */
  def getGuestport: Int = guestport

  /**
    * Returns the hostport
    *
    * @return the hostport
    */
  def getHostport: Int = hostport
}