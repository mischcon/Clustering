package vm.vagrant

import java.io.{File, IOException, PrintWriter}
import sbt._
import java.util.HashMap

import org.jruby.RubyObject
import org.jruby.embed.{LocalContextScope, ScriptingContainer}
import vm.java.configuration._
import vm.java.model.VagrantEnvironment

/**
  * Created by oliver.ziegert on 24.03.2017.
  */
class Vagrant(debug: Boolean = false){

  private var scriptingContainer: ScriptingContainer = null
  private var os: String = null

  def this(debug: Boolean) {
    this()
    scriptingContainer = new ScriptingContainer(LocalContextScope.SINGLETHREAD)
    os = if (System.getProperty("os.name").toLowerCase.contains("windows")) "windows" else "java"
    if (debug) this.debug()
  }

  private def debug() = {
    val currentEnv = scriptingContainer.getEnvironment
    val newEnv = new HashMap(currentEnv)
    newEnv.put("VAGRANT_LOG", "DEBUG")
    scriptingContainer.setEnvironment(newEnv)
  }

  def createEnvironment: VagrantEnvironment = {
    val vagrantEnv = scriptingContainer.runScriptlet(s"RUBY_PLATFORM = '$os'\n" +
      "require 'rubygems'\n" +
      "require 'vagrant-wrapper'\n" +
      "return Vagrant::Environment.new").asInstanceOf[RubyObject]
    new VagrantEnvironment(vagrantEnv)
  }

  def createEnvironment(path: File): VagrantEnvironment = {
    val vagrantEnv = scriptingContainer.runScriptlet(s"RUBY_PLATFORM = '$os'\n" +
      s"Dir.chdir(${path.getAbsolutePath})\n" +
      "require 'rubygems'\n" +
      "require 'vagrant-wrapper'\n" +
      "return Vagrant::Environment.new").asInstanceOf[RubyObject]
    new VagrantEnvironment(vagrantEnv)
  }

  @throws[IOException]
  def createEnvironment(path: File, environmentConfig: VagrantEnvironmentConfig): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(environmentConfig), null, null)

  @throws[IOException]
  def createEnvironment(path: File, environmentConfig: VagrantEnvironmentConfig, fileTemplates: Iterable[VagrantFileTemplateConfiguration]): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(environmentConfig), fileTemplates, null)

  @throws[IOException]
  def createEnvironment(path: File, environmentConfig: VagrantEnvironmentConfig, fileTemplates: Iterable[VagrantFileTemplateConfiguration], folderTemplates: Iterable[VagrantFolderTemplateConfiguration]): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(environmentConfig), fileTemplates, folderTemplates)

  @throws[IOException]
  def createEnvironment(path: File, configuration: VagrantConfiguration): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(configuration.getEnvironmentConfig), configuration.getFileTemplateConfigurations, configuration.getFolderTemplateConfigurations)

  @throws[IOException]
  def createEnvironment(path: File, vagrantfileContent: String, fileTemplates: Iterable[VagrantFileTemplateConfiguration], folderTemplates: Iterable[VagrantFolderTemplateConfiguration]): VagrantEnvironment = {
    path.mkdirs
    val vagrantFile = new File(path, "Vagrantfile")
    if (!vagrantFile.exists) vagrantFile.createNewFile
    val out = new PrintWriter(vagrantFile, "UTF-8")
    try { out.print(vagrantfileContent) } finally {out.close()}
    if (fileTemplates != null) {
      for (fileTemplate <- fileTemplates) {
        val fileInVagrantFolder = new File(path, fileTemplate.getPathInVagrantFolder)
        if (fileInVagrantFolder.getParentFile != null && !fileInVagrantFolder.getParentFile.exists) fileInVagrantFolder.getParentFile.mkdirs
        if (fileTemplate.useLocalFile) FileUtils.copyFile(fileTemplate.getLocalFile, fileInVagrantFolder)
        else FileUtils.copyURLToFile(fileTemplate.getUrlTemplate, fileInVagrantFolder)
      }
    }
    if (folderTemplates != null) {
      for (folderTemplate <- folderTemplates) {
        val folderInVagrantFolder = new File(path, folderTemplate.getPathInVagrantFolder)
        if (folderTemplate.useUriTemplate) FileUtils.copyDirectory(new File(folderTemplate.getUriTemplate), folderInVagrantFolder)
        else FileUtils.copyDirectory(folderTemplate.getLocalFolder, folderInVagrantFolder)
      }
    }
    createEnvironment(path)
  }

}
