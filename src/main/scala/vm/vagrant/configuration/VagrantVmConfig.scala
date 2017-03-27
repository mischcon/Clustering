package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */

import java.io.File
import java.net.URL
import java.util.UUID


/**
  * A configuration class that can be used to define and create a VM in Vagrant.
  *
  * @author oliver.ziegert
  *
  */
class VagrantVmConfig(val name: String,
                      val ip: String,
                      val hostName: String,
                      val boxName: String,
                      val boxUrl: URL,
                      val portForwardings: List[VagrantPortForwarding],
                      val puppetProvisionerConfig: PuppetProvisionerConfig,
                      val guiMode: Boolean,
                      val bootTimeout: Int,
                      val boxCheckUpdate: Boolean,
                      val boxDownloadChecksum: String,
                      val boxDownloadChecksumType:String,
                      val boxDownloadClientCert: File,
                      val boxDownloadCaCert: File,
                      val boxDownloadCaPath: File,
                      val boxDownloadInsecure: Boolean,
                      val boxDownloadLocationTrusted: Boolean,
                      val boxVersion: String,
                      val communicator:String,
                      val gracefulHaltTimeout: Int,
                      val guest: String,
                      val postUpMessage: String,
                      val usablePortRange: String,
                      val provider: VagrantProviderConfig)
