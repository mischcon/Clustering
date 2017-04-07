package vm.vagrant.configuration.builder

import vm.vagrant.configuration.builder.util.VagrantBuilderException
import vm.vagrant.configuration.{VagrantSyncedFolderConfig, VagrantSyncedFolderNfsConfig, VagrantSyncedFolderRsyncConfig, VagrantSyncedFolderVirtualBoxConfig}

/**
  * Created by oliver.ziegert on 28.03.17.
  */

object VagrantSyncedFoldersConfigBuilder {
  def createNfsConfig = new VagrantSyncedFolderNfsConfigBuilder
  def createVirtualBoxConfig = new VagrantSyncedFolderVirtualBoxConfigBuilder
  def createRsyncConfig = new VagrantSyncedFolderRsyncConfigBuilder
}

trait VagrantSyncedFoldersConfigBuilder {
  def build: VagrantSyncedFolderConfig
}

class VagrantSyncedFolderNfsConfigBuilder extends VagrantSyncedFoldersConfigBuilder {
  private var hostPath: String = _
  private var guestPath: String = _
  private var create: Boolean = false
  private var disabled: Boolean = false
  private var group: String = _
  private var mountOptions: Array[String] = _
  private var owner: String = _
  private var name: String = _
  private var nfsExport: Boolean = true
  private var nfsUdp: Boolean = true
  private var nfsVersion: Int = 3

  def withHostPath(hostPath: String): VagrantSyncedFolderNfsConfigBuilder = {
    this.hostPath = hostPath
    this
  }

  def withGuestPath(guestPath: String): VagrantSyncedFolderNfsConfigBuilder = {
    this.guestPath = guestPath
    this
  }

  def withCreate(create: Boolean): VagrantSyncedFolderNfsConfigBuilder = {
    this.create = create
    this
  }

  def withDisabled(disabled: Boolean): VagrantSyncedFolderNfsConfigBuilder = {
    this.disabled = disabled
    this
  }

  def withGroup(group: String): VagrantSyncedFolderNfsConfigBuilder = {
    this.group = group
    this
  }

  def withMountOptions(mountOptions: Array[String]): VagrantSyncedFolderNfsConfigBuilder = {
    this.mountOptions = mountOptions
    this
  }

  def withOwner(owner: String): VagrantSyncedFolderNfsConfigBuilder = {
    this.owner = owner
    this
  }

  def withName(name: String): VagrantSyncedFolderNfsConfigBuilder = {
    this.name = name
    this
  }

  def withNfsExport(nfsExport: Boolean): VagrantSyncedFolderNfsConfigBuilder = {
    this.nfsExport = nfsExport
    this
  }

  def withNfsUdp(nfsUdp: Boolean): VagrantSyncedFolderNfsConfigBuilder = {
    this.nfsUdp = nfsUdp
    this
  }

  def withNfsVersion(nfsVersion: Int): VagrantSyncedFolderNfsConfigBuilder = {
    this.nfsVersion = nfsVersion
    this
  }

  override def build: VagrantSyncedFolderConfig = {
    if (hostPath == null)
      throw new VagrantBuilderException("No hostPath defined")
    if (guestPath == null)
      throw new VagrantBuilderException("No guestPath defined")
    new VagrantSyncedFolderNfsConfig(_hostPath = hostPath,
                                     _guestPath = guestPath,
                                     _create = create,
                                     _disabled = disabled,
                                     _group = group,
                                     _mountOptions = mountOptions,
                                     _owner = owner,
                                     _name = name,
                                     nfsExport = nfsExport,
                                     nfsUdp = nfsUdp,
                                     nfsVersion = nfsVersion)
  }
}

class VagrantSyncedFolderVirtualBoxConfigBuilder extends VagrantSyncedFoldersConfigBuilder {
  private var hostPath: String = _
  private var guestPath: String = _
  private var create: Boolean = _
  private var disabled: Boolean = _
  private var group: String = _
  private var mountOptions: Array[String] = _
  private var owner: String = _
  private var name: String = _

  def withHostPath(hostPath: String): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.hostPath = hostPath
    this
  }

  def withGuestPath(guestPath: String): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.guestPath = guestPath
    this
  }

  def withCreate(create: Boolean): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.create = create
    this
  }

  def withDisabled(disabled: Boolean): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.disabled = disabled
    this
  }

  def withGroup(group: String): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.group = group
    this
  }

  def withMountOptions(mountOptions: Array[String]): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.mountOptions = mountOptions
    this
  }

  def withOwner(owner: String): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.owner = owner
    this
  }

  def withName(name: String): VagrantSyncedFolderVirtualBoxConfigBuilder = {
    this.name = name
    this
  }

  override def build: VagrantSyncedFolderConfig = {
    if (hostPath == null && guestPath == null)
      throw new VagrantBuilderException("HostPath and GuestPath is required!")
    new VagrantSyncedFolderVirtualBoxConfig(_hostPath = hostPath,
                                            _guestPath = guestPath,
                                            _create = create,
                                            _disabled = disabled,
                                            _group = group,
                                            _mountOptions = mountOptions,
                                            _owner = owner,
                                            _name = name)
  }
}

class VagrantSyncedFolderRsyncConfigBuilder extends VagrantSyncedFoldersConfigBuilder {
  private var hostPath: String = _
  private var guestPath: String = _
  private var create: Boolean = _
  private var disabled: Boolean = _
  private var group: String = _
  private var mountOptions: Array[String] = _
  private var owner: String = _
  private var name: String = _

  def withHostPath(hostPath: String): VagrantSyncedFolderRsyncConfigBuilder = {
    this.hostPath = hostPath
    this
  }

  def withGuestPath(guestPath: String): VagrantSyncedFolderRsyncConfigBuilder = {
    this.guestPath = guestPath
    this
  }

  def withCreate(create: Boolean): VagrantSyncedFolderRsyncConfigBuilder = {
    this.create = create
    this
  }

  def withDisabled(disabled: Boolean): VagrantSyncedFolderRsyncConfigBuilder = {
    this.disabled = disabled
    this
  }

  def withGroup(group: String): VagrantSyncedFolderRsyncConfigBuilder = {
    this.group = group
    this
  }

  def withMountOptions(mountOptions: Array[String]): VagrantSyncedFolderRsyncConfigBuilder = {
    this.mountOptions = mountOptions
    this
  }

  def withOwner(owner: String): VagrantSyncedFolderRsyncConfigBuilder = {
    this.owner = owner
    this
  }

  def withName(name: String): VagrantSyncedFolderRsyncConfigBuilder = {
    this.name = name
    this
  }

  override def build: VagrantSyncedFolderConfig = {
    if (hostPath == null && guestPath == null)
      throw new VagrantBuilderException("HostPath and GuestPath is required!")
    new VagrantSyncedFolderRsyncConfig(_hostPath = hostPath,
                                       _guestPath = guestPath,
                                       _create = create,
                                       _disabled = disabled,
                                       _group = group,
                                       _mountOptions = mountOptions,
                                       _owner = owner,
                                       _name = name)
  }
}