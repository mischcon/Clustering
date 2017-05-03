package vm

import java.io.File
import java.lang.management.ManagementFactory
import javax.management.{Attribute, ObjectName}

import akka.actor.{Actor, ActorLogging, ActorRef}
import org.jruby.RubyObject
import org.jruby.embed.{LocalContextScope, ScriptingContainer}
import utils.messages.{DeregisterNodeMonitorActor, RegisterNodeMonitorActor, SystemAttributes}
import vm.messages._

import scala.collection.JavaConverters._


/**
  * Created by oliver.ziegert on 18.04.17.
  */
class NodeMonitorActor extends Actor with ActorLogging {

  private var path: File = _
  private var vagrant: Boolean = _
  private var globalStatusActor: ActorRef = _
  private var nodeActor: ActorRef = context.parent
  private val mbeanServer = ManagementFactory.getPlatformMBeanServer()
  val attributes = Map(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME -> Array("FreePhysicalMemorySize",
                                                                               "TotalPhysicalMemorySize",
                                                                               "Name",
                                                                               "AvailableProcessors",
                                                                               "SystemCpuLoad",
                                                                               "Arch",
                                                                               "Version"),
                       ManagementFactory.RUNTIME_MXBEAN_NAME -> Array("VmName",
                                                                      "SpecVersion"))

  self ! Init

  override def receive: Receive = {
    case Init => init
    case GetSystemAttributes => sender() ! SystemAttributes(getSystemAttributes)
    case SetPath(path) => this.path = path
    case SetGlobalStatusActor(globalStatusActor) => {
      log.debug("received GlobalStatusActor")
      this.globalStatusActor = globalStatusActor
      globalStatusActor ! RegisterNodeMonitorActor
    }
  }

  def init = {
    nodeActor ! GetGlobalStatusActor
    vagrant = checkVagrant
    log.debug("init done")
  }

  def getSystemAttributes: Map[String,String] = {
    log.debug(s"getSystemAttributes called from ${sender()}")
    var systemAttributes = attributes.map(attribute => mbeanServer.getAttributes(new ObjectName(attribute._1), attribute._2).asScala.map(_.asInstanceOf[Attribute])).flatten.toList
    if (path != null) {
      systemAttributes :+= new Attribute("TotalSpace", path.getTotalSpace)
      systemAttributes :+= new Attribute("FreeSpace", path.getFreeSpace)
    }
    systemAttributes :+= new Attribute("Vagrant", vagrant)
    systemAttributes.groupBy(_.getName).map{case (k, v) => k -> v.head.getValue.toString}
  }

  def checkVagrant: Boolean = {
    log.debug("checkVagrant called")
    val scriptingContainer: ScriptingContainer = new ScriptingContainer(LocalContextScope.SINGLETHREAD)
    val os = if (System.getProperty("os.name").toLowerCase.contains("windows")) "windows" else "java"
    scriptingContainer.put("RUBY_PLATFORM", os)
    try {
      val vagrantEnv = scriptingContainer.runScriptlet("require 'rubygems'\n" +
        "require 'vagrant-wrapper'\n" +
        "return VagrantWrapper.require_or_help_install('>= 1.1')").asInstanceOf[RubyObject]
      return true
    } catch {
      case exception: ClassCastException =>
        log.debug(exception.getMessage)
        return false
    }
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    globalStatusActor ! DeregisterNodeMonitorActor
    log.debug(s"goodbye from ${self.path.name}")
  }
}
