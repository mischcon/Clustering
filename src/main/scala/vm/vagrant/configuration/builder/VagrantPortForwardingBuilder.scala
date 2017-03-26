package vm.vagrant.configuration.builder

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import vm.vagrant.configuration.VagrantPortForwarding
import vm.vagrant.configuration.builder.util.VagrantBuilderException


object VagrantPortForwardingBuilder {
  def create = new VagrantPortForwardingBuilder
}

class VagrantPortForwardingBuilder() {
  private var guestport: Int = _
  private var hostport: Int = _
  private var name: String = _
  private var protocol: String = _

  def withHostPort(hostport: Int): VagrantPortForwardingBuilder = {
    this.hostport = hostport
    this
  }

  def withGuestPort(guestport: Int): VagrantPortForwardingBuilder = {
    this.guestport = guestport
    this
  }

  def withName(name: String): VagrantPortForwardingBuilder = {
    this.name = name
    this
  }

  def withProtocol(protocol: String): VagrantPortForwardingBuilder = {
    this.protocol = protocol
    this
  }

  def build: VagrantPortForwarding = {
    if (guestport < 0) throw new VagrantBuilderException("no guestport defined")
    if (hostport < 0) throw new VagrantBuilderException("no hostport defined")
    new VagrantPortForwarding(name, guestport, hostport, protocol)
  }
}
