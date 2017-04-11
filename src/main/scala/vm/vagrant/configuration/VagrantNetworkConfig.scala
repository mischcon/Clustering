package vm.vagrant.configuration

import vm.vagrant.configuration.Protocol.Protocol
import vm.vagrant.configuration.Service.Service
/**
  * Created by oliver.ziegert on 28.03.17.
  */
trait VagrantNetworkConfig {
  def mode: String
  def isComplete: Boolean
}

class VagrantPrivateNetworkConfig(var dhcp: Boolean,
                                  var ip: String,
                                  var netmask: String,
                                  var autoConfig: Boolean) extends VagrantNetworkConfig {
  override def mode: String = "private_network"

  override def isComplete: Boolean = (dhcp || (ip != null && !ip.isEmpty && netmask != null && !netmask.isEmpty))
}

class VagrantPortForwardingConfig (var autoCorrect: Boolean,
                                   var guestPort: Int,
                                   var guestIp: String,
                                   var hostPort: Int,
                                   var hostIp: String,
                                   var protocol: Protocol,
                                   var service: Service,
                                   var name: String) extends VagrantNetworkConfig {
  override def mode: String = "forwarded_port"

  override def isComplete: Boolean = (guestPort > 0 && hostPort > 0)
}

class VagrantPublicNetworkConfig (var dhcp: Boolean,
                                  var useDhcpAssignedDefaultRoute: Boolean,
                                  var ip: String,
                                  var bridges: List[String],
                                  var autoConfig: Boolean) extends VagrantNetworkConfig {
  override def mode: String = "public_network"

  override def isComplete: Boolean = true
}

object Protocol extends Enumeration{
  type Protocol = Value
  val udp = Value("udp")
  val tcp = Value("tcp")
}

object Service extends Enumeration {
  type Service = Value
  val http = Value("http")
  val https = Value("https")
  val ssh = Value("ssh")
  val mysql = Value("mysql")
}
