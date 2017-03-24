package vm
import org.jruby.embed.ScriptingContainer

import collection.JavaConverters._

/**
  * Created by oliver.ziegert on 22.03.2017.
  */

class test {
  println("[" + getClass.getName + "]")
  val whereami = System.getProperty("user.dir")
  println("[CWD: " + whereami + "]")
  val scriptingContainer = new ScriptingContainer
  val os = if (System.getProperty("os.name").toLowerCase.contains("windows")) "windows" else "java"
  println("[OS: " + os + "]")
  scriptingContainer.runScriptlet(s"RUBY_PLATFORM = '$os'")
  val script: String = "require 'vagrant-wrapper'\n\n#########################################\n# Config\n#########\n\nCWD = 'C:/dev/vm/ruby.ssp-nb067.local'\n\n##########################################\n\n\nDir.chdir(CWD)\n\nvw = VagrantWrapper.require_or_help_install('>= 1.1')\nputs vw.vagrant_version\nputs vw.vagrant_location\nputs vw.get_output 'box list'\nputs vw.get_output 'status'\nputs vw.get_output 'halt'\nputs vw.get_output 'status'\nputs vw.get_output 'up'\nputs vw.get_output 'status'"
  scriptingContainer.runScriptlet(script)
}

object test extends App{
  new test
}