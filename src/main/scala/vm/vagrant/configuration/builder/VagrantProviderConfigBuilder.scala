package vm.vagrant.configuration.builder

import vm.vagrant.configuration.VagrantProviderConfig
import vm.vagrant.configuration.builder.util.VagrantBuilderException

/**
  * Created by oliver.ziegert on 31.03.17.
  */
object VagrantProviderConfigBuilder {
  def create = new VagrantProviderConfigBuilder
}

class VagrantProviderConfigBuilder {
  private var name: String = _
  private var guiMode: Boolean = false
  private var memory: Int = _
  private var cpus: Int = _
  private var customize: List[String] = List()
  private var vmName: String = _

  def withName(name: String): VagrantProviderConfigBuilder = {
    this.name = name
    this
  }

  def withGuiMode(guiMode: Boolean): VagrantProviderConfigBuilder = {
    this.guiMode = guiMode
    this
  }

  def withMemory(memory: Int): VagrantProviderConfigBuilder = {
    this.memory = memory
    this
  }

  def withCpus(cpus: Int): VagrantProviderConfigBuilder = {
    this.cpus = cpus
    this
  }

  def withCustomize(customize: String): VagrantProviderConfigBuilder = {
    this.customize ::= customize
    this
  }

  def withVmName(vmName: String): VagrantProviderConfigBuilder = {
    this.vmName = vmName
    this
  }

  def build(): VagrantProviderConfig = {
    if (name != null && name.isEmpty) throw new VagrantBuilderException("No name defined")
    new VagrantProviderConfig(name = name,
                              guiMode = guiMode,
                              memory = memory,
                              cpus = cpus,
                              customize = customize,
                              vmName = vmName)
  }


}
