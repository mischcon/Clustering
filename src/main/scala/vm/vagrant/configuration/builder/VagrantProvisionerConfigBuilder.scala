package vm.vagrant.configuration.builder

import java.io.File

import vm.vagrant.configuration.Run.Run
import vm.vagrant.configuration.{Run, VagrantProvisionerConfig, VagrantProvisionerFileConfig, VagrantProvisionerShellConfig}

/**
  * Created by oliver.ziegert on 02.04.17.
  */
object VagrantProvisionerConfigBuilder {
  def createFileConfig: VagrantProvisionerFileConfigBuilder = new VagrantProvisionerFileConfigBuilder
  def createShellConfig: VagrantProvisionerShellConfigBuilder = new VagrantProvisionerShellConfigBuilder
}

trait VagrantProvisionerConfigBuilder {
  def build: VagrantProvisionerConfig
}

class VagrantProvisionerFileConfigBuilder extends VagrantProvisionerConfigBuilder {
  private var run: Run = Run.once
  private var preserveOrder: Boolean = false
  private var source: File = _
  private var destination: File = _


  def withRun(run: Run): VagrantProvisionerFileConfigBuilder = {
    this.run = run
    this
  }

  def withPreserveOrder(preserveOrder: Boolean): VagrantProvisionerFileConfigBuilder = {
    this.preserveOrder = preserveOrder
    this
  }

  def withSource(source: File): VagrantProvisionerFileConfigBuilder = {
    this.source = source
    this
  }

  def withDestination(destination: File): VagrantProvisionerFileConfigBuilder = {
    this.destination = destination
    this
  }



  override def build: VagrantProvisionerConfig = {
    new VagrantProvisionerFileConfig(_run = run,
                                     _preserveOrder = preserveOrder,
                                     source = source,
                                     destination = destination)
  }
}

class VagrantProvisionerShellConfigBuilder extends VagrantProvisionerConfigBuilder {
  private var run: Run = Run.once
  private var preserveOrder: Boolean = false
  private var inline: String = _
  private var path: File = _
  private var args: List[String] = List()
  private var binary: Boolean = _
  private var privileged: Boolean = true
  private var uploadPath: File = _
  private var keepColor: Boolean = _
  private var name: String = _
  private var md5: String = _
  private var sha1: String = _

  def withRun(run: Run): VagrantProvisionerShellConfigBuilder = {
    this.run = run
    this
  }

  def withPreserveOrder(preserveOrder: Boolean): VagrantProvisionerShellConfigBuilder = {
    this.preserveOrder = preserveOrder
    this
  }

  def withInline(inline: String): VagrantProvisionerShellConfigBuilder = {
    this.inline = inline
    this
  }

  def withPath(path: File): VagrantProvisionerShellConfigBuilder = {
    this.path = path
    this
  }

  def withArg(arg: String): VagrantProvisionerShellConfigBuilder = {
    this.args ::= arg
    this
  }

  def withBinary(binary: Boolean): VagrantProvisionerShellConfigBuilder = {
    this.binary = binary
    this
  }

  def withPrivileged(privileged: Boolean): VagrantProvisionerShellConfigBuilder = {
    this.privileged = privileged
    this
  }

  def withUploadPath(uploadPath: File): VagrantProvisionerShellConfigBuilder = {
    this.uploadPath = uploadPath
    this
  }

  def withKeepColor(keepColor: Boolean): VagrantProvisionerShellConfigBuilder = {
    this.keepColor = keepColor
    this
  }

  def withName(name: String): VagrantProvisionerShellConfigBuilder = {
    this.name = name
    this
  }

  def withMd5(md5: String): VagrantProvisionerShellConfigBuilder = {
    this.md5 = md5
    this
  }

  def withSha1(sha1: String): VagrantProvisionerShellConfigBuilder = {
    this.sha1 = sha1
    this
  }

  override def build: VagrantProvisionerConfig = {
    new VagrantProvisionerShellConfig(_run = run,
                                      _preserveOrder = preserveOrder,
                                      inline = inline,
                                      path = path,
                                      args = args,
                                      binary = binary,
                                      privileged = privileged,
                                      uploadPath = uploadPath,
                                      keepColor = keepColor,
                                      name = name,
                                      md5 = md5,
                                      sha1 = sha1)
  }
}