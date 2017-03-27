package vm.vagrant.configuration

/**
  * Created by oliver.ziegert on 24.03.17.
  */
/**
  * Configuration for a puppet provisioner that is used by Vagrant to configure a
  * VM
  *
  * @author oliver.ziegert
  *
  */
class VagrantProviderConfig(val name : String = "virtualbox",
                            val guiMode: Boolean = false,
                            val memory: Int,
                            val cpus: Int,
                            val customize: List[String],
                            val vmName: String)