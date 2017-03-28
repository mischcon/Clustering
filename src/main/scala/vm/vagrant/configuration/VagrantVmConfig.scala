package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.URL

import vm.vagrant.configuration.ChecksumType.ChecksumType

/**
  * A configuration class that can be used to define and create a VM in Vagrant.
  *
  * @author oliver.ziegert
  *
  */
class VagrantVmConfig(var name: String,
                      var hostName: String,
                      var boxName: String,
                      var boxUrl: URL,
                      var vagrantProvisionerConfigs: List[VagrantProvisionerConfig],
                      var vagrantNetworkConfigs: List[VagrantNetworkConfig],
                      var vagrantSyncedFolderConfigs: List[VagrantSyncedFolderConfig],
                      var guiMode: Boolean,
                      var bootTimeout: Int,
                      var boxCheckUpdate: Boolean,
                      var boxDownloadChecksum: String,
                      var boxDownloadChecksumType: ChecksumType,
                      var boxDownloadClientCert: File,
                      var boxDownloadCaCert: File,
                      var boxDownloadCaPath: File,
                      var boxDownloadInsecure: Boolean,
                      var boxDownloadLocationTrusted: Boolean,
                      var boxVersion: String,
                      var communicator: String,
                      var gracefulHaltTimeout: Int,
                      var guest: String,
                      var postUpMessage: String,
                      var usablePortRange: String,
                      var provider: VagrantProviderConfig)

object ChecksumType extends Enumeration{
  type ChecksumType = Value
  val md5, sha1, sha256 = Value
}
