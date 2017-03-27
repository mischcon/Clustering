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
class VagrantPortForwarding(var name: String,
                            var guestport: Int,
                            var hostport: Int,
                            var protocol: String) {

  def isComplete: Boolean = if (guestport != 0 && hostport != 0) true else false
}