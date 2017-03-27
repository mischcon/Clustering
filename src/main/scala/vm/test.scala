package vm
import java.io.File

import org.jruby.embed.ScriptingContainer
import vm.vagrant.Vagrant
import vm.vagrant.configuration.builder.{VagrantConfigurationBuilder, VagrantEnvironmentConfigBuilder, VagrantVmConfigBuilder}

import collection.JavaConverters._

/**
  * Created by oliver.ziegert on 22.03.2017.
  */

class test {
  println("[" + getClass.getName + "]")
  val whereami = System.getProperty("user.dir")
  println("[CWD: " + whereami + "]")
  //val scriptingContainer = new ScriptingContainer
  val os = if (System.getProperty("os.name").toLowerCase.contains("windows")) "windows" else "java"
  println("[OS: " + os + "]")
  //scriptingContainer.runScriptlet(s"RUBY_PLATFORM = '$os'")
  //val script: String = "require 'vagrant-wrapper'\n\n#########################################\n# Config\n#########\n\nCWD = 'C/Users/oliver.ziegert/Documents/vms/scala.pc-ziegert.local'\n\n##########################################\n\n\nDir.chdir(CWD)\n\nvw = VagrantWrapper.require_or_help_install('>= 1.1')\nputs vw.vagrant_version\nputs vw.vagrant_location\nputs vw.get_output 'box list'\nputs vw.get_output 'status'\nputs vw.get_output 'halt'\nputs vw.get_output 'status'\nputs vw.get_output 'up'\nputs vw.get_output 'status'"
  //scriptingContainer.runScriptlet(script)


  val vmConfig = VagrantVmConfigBuilder.create.withName("dev-box").withDevBox.withHostOnlyIp("192.168.3.3").build
  val environmentConfig = VagrantEnvironmentConfigBuilder.create.withVagrantVmConfig(vmConfig).build
  val configuration = VagrantConfigurationBuilder.create.withVagrantEnvironmentConfig(environmentConfig).build
  val vagrant = new Vagrant().createEnvironment(new File("/Volumes/Daten/Vagrant/scala.local"), configuration)
  vagrant.up
  vagrant.destroy
}

object test extends App{
  new test
}