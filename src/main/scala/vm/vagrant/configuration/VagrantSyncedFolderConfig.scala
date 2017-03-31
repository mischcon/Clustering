package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 28.03.17.
  */

trait VagrantSyncedFolderConfig {
  def hostPath: String
  def guestPath: String
  def mode: String
  def create: Boolean
  def disabled: Boolean
  def group: String
  def mountOptions: Array[String]
  def owner: String
  def name: String
}
class VagrantSyncedFolderNfsConfig(var _hostPath: String,
                                   var _guestPath: String,
                                   var _create: Boolean,
                                   var _disabled: Boolean,
                                   var _group: String,
                                   var _mountOptions: Array[String],
                                   var _owner: String,
                                   var _name: String,
                                   var nfsExport: Boolean,
                                   var nfsUdp: Boolean,
                                   var nfsVersion: Int) extends VagrantSyncedFolderConfig {
  override def hostPath: String = _hostPath
  override def guestPath: String = _guestPath
  override def mode: String = "nfs"
  override def create: Boolean = this._create
  override def disabled: Boolean = this._disabled
  override def group: String = this._group
  override def mountOptions: Array[String] = this._mountOptions
  override def owner: String = this._owner
  override def name: String = this._name
}
class VagrantSyncedFolderVirtualBoxConfig(var _hostPath: String,
                                          var _guestPath: String,
                                          var _create: Boolean,
                                          var _disabled: Boolean,
                                          var _group: String,
                                          var _mountOptions: Array[String],
                                          var _owner: String,
                                          var _name: String) extends VagrantSyncedFolderConfig {
  override def hostPath: String = _hostPath
  override def guestPath: String = _guestPath
  override def mode: String = "virtualbox"
  override def create: Boolean = this._create
  override def disabled: Boolean = this._disabled
  override def group: String = this._group
  override def mountOptions: Array[String] = this._mountOptions
  override def owner: String = this._owner
  override def name: String = this._name
}

class VagrantSyncedFolderRsyncConfig(var _hostPath: String,
                                     var _guestPath: String,
                                     var _create: Boolean,
                                     var _disabled: Boolean,
                                     var _group: String,
                                     var _mountOptions: Array[String],
                                     var _owner: String,
                                     var _name: String) extends VagrantSyncedFolderConfig {
  override def hostPath: String = _hostPath
  override def guestPath: String = _guestPath
  override def mode: String = "rsync"
  override def create: Boolean = this._create
  override def disabled: Boolean = this._disabled
  override def group: String = this._group
  override def mountOptions: Array[String] = this._mountOptions
  override def owner: String = this._owner
  override def name: String = this._name
}
