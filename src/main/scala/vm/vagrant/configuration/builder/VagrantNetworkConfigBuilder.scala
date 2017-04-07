package vm.vagrant.configuration.builder

import vm.vagrant.configuration.Protocol.Protocol
import vm.vagrant.configuration._


/**
  * Created by oliver.ziegert on 28.03.17.
  */

object VagrantNetworkConfigBuilder {
  def createPrivateNetworkConfig = new VagrantPrivateNetworkConfigBuilder
  def createPortForwardingConfig = new VagrantPortForwardingConfigBuilder
  def createPublicNetworkConfig = new VagrantPublicNetworkConfigBuilder
}

trait VagrantNetworkConfigBuilder {
  def build: VagrantNetworkConfig
}

class VagrantPrivateNetworkConfigBuilder extends VagrantNetworkConfigBuilder{
  private var dhcp: Boolean = _
  private var ip: String = _
  private var netmask: String = _
  private var autoConfig: Boolean = true

  def withDhcp(dhcp: Boolean): VagrantPrivateNetworkConfigBuilder = {
    this.dhcp = dhcp
    this
  }

  def withIp(ip: String): VagrantPrivateNetworkConfigBuilder = {
    this.ip = ip
     this
  }

  def withNetmask(netmask: String): VagrantPrivateNetworkConfigBuilder = {
    this.netmask = netmask
    this
  }

  def withAutoConfig(autoConfig: Boolean): VagrantPrivateNetworkConfigBuilder = {
    this.autoConfig = autoConfig
    this
  }

  override def build: VagrantNetworkConfig = {
    new VagrantPrivateNetworkConfig(dhcp = dhcp,
                                    ip = ip,
                                    netmask = netmask,
                                    autoConfig = autoConfig)
  }
}

class VagrantPortForwardingConfigBuilder extends VagrantNetworkConfigBuilder {
  private var autoCorrect: Boolean = _
  private var guestPort: Int = _
  private var guestIp: String = _
  private var hostPort: Int = _
  private var hostIp: String = _
  private var protocol: Protocol = Protocol.tcp
  private var name: String = _

  def withAutoCorrect(autoCorrect: Boolean): VagrantPortForwardingConfigBuilder = {
    this.autoCorrect = autoCorrect
    this
  }

  def withGuestPort(guestPort: Int): VagrantPortForwardingConfigBuilder = {
    this.guestPort = guestPort
    this
  }

  def withGuestIp(guestIp: String): VagrantPortForwardingConfigBuilder = {
    this.guestIp = guestIp
    this
  }

  def withHostPort(hostPort: Int): VagrantPortForwardingConfigBuilder = {
    this.hostPort = hostPort
    this
  }


  def withHostIp(hostIp: String): VagrantPortForwardingConfigBuilder = {
    this.hostIp = hostIp
    this
  }


  def withProtocol(protocol: Protocol): VagrantPortForwardingConfigBuilder = {
    this.protocol = protocol
    this
  }

  def withName(name: String): VagrantPortForwardingConfigBuilder = {
    this.name = name
    this
  }

  override def build: VagrantNetworkConfig = {
    new VagrantPortForwardingConfig(autoCorrect = autoCorrect,
                                    guestPort = guestPort,
                                    guestIp = guestIp,
                                    hostPort = hostPort,
                                    hostIp = hostIp,
                                    protocol = protocol,
                                    name = name)
  }
}

class VagrantPublicNetworkConfigBuilder extends VagrantNetworkConfigBuilder {
  private var dhcp: Boolean = _
  private var useDhcpAssignedDefaultRoute: Boolean = _
  private var ip: String = _
  private var bridges: List[String] = List()
  private var autoConfig: Boolean = true

  def withDhcp(dhcp: Boolean): VagrantPublicNetworkConfigBuilder = {
    this.dhcp = dhcp
    this
  }

  def withUseDhcpAssignedDefaultRoute(useDhcpAssignedDefaultRoute: Boolean): VagrantPublicNetworkConfigBuilder = {
    this.useDhcpAssignedDefaultRoute = useDhcpAssignedDefaultRoute
    this
  }

  def withIp(ip: String): VagrantPublicNetworkConfigBuilder = {
    this.ip = ip
    this
  }

  def withBridge(bridge: String): VagrantPublicNetworkConfigBuilder = {
    this.bridges ::= bridge
    this
  }

  def withAutoConfig(autoConfig: Boolean): VagrantPublicNetworkConfigBuilder = {
    this.autoConfig = autoConfig
    this
  }
  override def build: VagrantNetworkConfig = {
    new VagrantPublicNetworkConfig(dhcp = dhcp,
                                   useDhcpAssignedDefaultRoute = useDhcpAssignedDefaultRoute,
                                   ip = ip,
                                   bridges = bridges,
                                   autoConfig = autoConfig)
  }
}