package de.oth.clustering.scala.vm.vagrant

import java.io.{File, IOException}
import java.nio.charset.Charset

import de.oth.clustering.java.vm.vagrant.configuration.{VagrantConfiguration, VagrantEnvironmentConfig, VagrantFileTemplateConfiguration, VagrantFolderTemplateConfiguration}
import de.oth.clustering.scala.vm.vagrant.configuration._
import de.oth.clustering.scala.vm.vagrant.model.VagrantEnvironment
import org.jruby.RubyObject
import org.jruby.embed.{LocalContextScope, ScriptingContainer}
import sbt.io.IO.{copyDirectory, copyFile, write}

import scala.collection.JavaConverters._
import scala.io.Source.fromURL

/**
  * Created by oliver.ziegert on 24.03.2017.
  */

class Vagrant(debug: Boolean = false){

  private val scriptingContainer: ScriptingContainer = new ScriptingContainer(LocalContextScope.THREADSAFE)
  if (debug) this.debugVM()

  private def debugVM() = {
    val currentEnv = scriptingContainer.getEnvironment.asScala.map(d => d._1.toString -> d._2.toString)
    val newEnv = currentEnv + ("VAGRANT_LOG" -> "DEBUG")
    scriptingContainer.setEnvironment(newEnv.asJava)
  }

  def createEnvironment: VagrantEnvironment = {
    val os = if (System.getProperty("os.name").toLowerCase.contains("windows")) "windows" else "java"
    scriptingContainer.put("RUBY_PLATFORM", os)
    val vagrantEnv = scriptingContainer.runScriptlet("require 'rubygems'\n" +
      "require 'vagrant-wrapper'\n" +
      "return VagrantWrapper.require_or_help_install('>= 1.1')").asInstanceOf[RubyObject]
    new VagrantEnvironment(vagrantEnv)
  }

  def createEnvironment(path: File): VagrantEnvironment = {
    val currentEnv = scriptingContainer.getEnvironment.asScala.map(d => d._1.toString -> d._2.toString)
    val newEnv = currentEnv + ("VAGRANT_CWD" -> path.getAbsolutePath)
    scriptingContainer.setEnvironment(newEnv.asJava)
    createEnvironment
  }

  @throws[IOException]
  def createEnvironment(path: File, environmentConfig: VagrantEnvironmentConfig): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(environmentConfig), null, null)

  @throws[IOException]
  def createEnvironment(environmentConfig: VagrantEnvironmentConfig): VagrantEnvironment = createEnvironment(environmentConfig.path, VagrantConfigurationUtilities.createVagrantFileContent(environmentConfig), null, null)

  @throws[IOException]
  def createEnvironment(path: File, environmentConfig: VagrantEnvironmentConfig, fileTemplates: Iterable[VagrantFileTemplateConfiguration]): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(environmentConfig), fileTemplates, null)

  @throws[IOException]
  def createEnvironment(path: File, environmentConfig: VagrantEnvironmentConfig, fileTemplates: Iterable[VagrantFileTemplateConfiguration], folderTemplates: Iterable[VagrantFolderTemplateConfiguration]): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(environmentConfig), fileTemplates, folderTemplates)

  @throws[IOException]
  def createEnvironment(path: File, configuration: VagrantConfiguration): VagrantEnvironment = createEnvironment(path, VagrantConfigurationUtilities.createVagrantFileContent(configuration.environmentConfig()), configuration.fileTemplateConfigurations().asScala, configuration.folderTemplateConfigurations().asScala)

  @throws[IOException]
  def createEnvironment(path: File, vagrantfileContent: String, fileTemplates: Iterable[VagrantFileTemplateConfiguration], folderTemplates: Iterable[VagrantFolderTemplateConfiguration]): VagrantEnvironment = {
    path.mkdirs
    val vagrantFile = new File(path, "Vagrantfile")
    if (!vagrantFile.exists) vagrantFile.createNewFile
    write(vagrantFile, vagrantfileContent, Charset.forName("UTF-8"), false)
    if (fileTemplates != null) {
      for (fileTemplate <- fileTemplates) {
        val fileInVagrantFolder = new File(path, fileTemplate.getPathInVagrantFolder)
        if (fileInVagrantFolder.getParentFile != null && !fileInVagrantFolder.getParentFile.exists) fileInVagrantFolder.getParentFile.mkdirs
        if (fileTemplate.useLocalFile) copyFile(fileTemplate.getLocalFile, fileInVagrantFolder)
        else write(fileInVagrantFolder, fromURL(fileTemplate.getUrlTemplate).mkString, Charset.forName("UTF-8"), false)
      }
    }
    if (folderTemplates != null) {
      for (folderTemplate <- folderTemplates) {
        val folderInVagrantFolder = new File(path, folderTemplate.getPathInVagrantFolder)
        if (folderTemplate.useUriTemplate) copyDirectory(new File(folderTemplate.getUriTemplate), folderInVagrantFolder, true)
        else copyDirectory(folderTemplate.getLocalFolder, folderInVagrantFolder, true)
      }
    }
    createEnvironment(path)
  }
}
