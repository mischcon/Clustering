package vm
import java.io.File


import vm.vagrant.Vagrant
import vm.vagrant.configuration.builder.{VagrantConfigurationBuilder, VagrantEnvironmentConfigBuilder, VagrantVmConfigBuilder}



/**
  * Created by oliver.ziegert on 22.03.2017.
  */

class test {



  val vmConfig = VagrantVmConfigBuilder
    .create
    .build

  val environmentConfig = VagrantEnvironmentConfigBuilder
    .create
    .withVagrantVmConfig(vmConfig)
    .build

  val vagrant = new Vagrant().createEnvironment(new File("/Volumes/Daten/Vagrant/scala.local"), environmentConfig)





  vagrant.up
  vagrant.destroy
}

object test extends App{
  new test
}