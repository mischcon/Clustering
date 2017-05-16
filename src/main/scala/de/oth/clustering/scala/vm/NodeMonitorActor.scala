package de.oth.clustering.scala.vm

import java.io.File
import java.lang.management.ManagementFactory
import javax.management.{Attribute, MBeanServer, ObjectName}

import akka.actor.{Actor, ActorLogging, ActorRef}
import de.oth.clustering.scala.utils.messages.{DeregisterNodeMonitorActor, RegisterNodeMonitorActor, SystemAttributes}
import de.oth.clustering.scala.vm.messages._
import org.jruby.RubyObject
import org.jruby.embed.{LocalContextScope, ScriptingContainer}

import scala.collection.JavaConverters._
import scala.concurrent.duration.{DurationInt, FiniteDuration}


/**
  * Created by oliver.ziegert on 18.04.17.
  */
class NodeMonitorActor extends Actor with ActorLogging {

  private var path: File = _
  private var vagrant: Boolean = _
  private var globalStatusActor: ActorRef = _
  private var nodeActor: ActorRef = _
  private var ready:Boolean = _
  private var mbeanServer: MBeanServer = _
  private val attributes = Map(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME -> Array("FreePhysicalMemorySize",
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
    case Init                                   => log.debug("got Init");                          handlerInit
    case NotReadyJet(message)                   => log.debug(s"got NotReadyJet($message)");        handlerNotReadyJet(message)
    case GetSystemAttributes          if ready  => log.debug("got GetSystemAttributes");           handlerGetSystemAttributes
    case SetPath(path)                          => log.debug(s"got SetPath($path)");               handlerSetPath(path)
    case SetGlobalStatusActor(actor)            => log.debug(s"got SetGlobalStatusActor($actor)"); handlerSetGlobalStatusActor(actor)
    case x: Any                       if !ready => log.debug(s"got Message $x but NotReadyJet");   handlerNotReady(x)
  }

  private def handlerInit = {
    ready = false
    nodeActor = context.parent
    mbeanServer = ManagementFactory.getPlatformMBeanServer()
    nodeActor ! GetGlobalStatusActor
  }

  private def handlerGetSystemAttributes = {
    var systemAttributes = attributes.map(
      attribute => mbeanServer.getAttributes(
        new ObjectName(
          attribute._1),
          attribute._2)
        .asScala.map(
          _.asInstanceOf[Attribute]))
        .flatten.toList
    if (path != null) {
      systemAttributes :+= new Attribute("TotalSpace", path.getTotalSpace)
      systemAttributes :+= new Attribute("FreeSpace", path.getFreeSpace)
    }
    systemAttributes :+= new Attribute("Vagrant", vagrant)
    sender() ! SystemAttributes(systemAttributes.groupBy(_.getName).map{case (k, v) => k -> v.head.getValue.toString})
  }

  private def handlerSetPath(path: File) = {
    this.path = path
  }

  private def handlerSetGlobalStatusActor(actorRef: ActorRef) = {
    this.globalStatusActor = actorRef
    vagrant = checkVagrant
    ready = true
    globalStatusActor ! RegisterNodeMonitorActor(self)
  }

  private def handlerNotReady(any: Any) = {
    sender() ! NotReadyJet(any)
  }

  def checkVagrant: Boolean = {
    val scriptingContainer: ScriptingContainer = new ScriptingContainer(LocalContextScope.THREADSAFE)
    val os = if (System.getProperty("os.name").toLowerCase.contains("windows")) "windows" else "java"
    scriptingContainer.put("RUBY_PLATFORM", os)
    try {
      val vagrantEnv = scriptingContainer.runScriptlet("require 'rubygems'\n" +
        "require 'vagrant-wrapper'\n" +
        "return VagrantWrapper.require_or_help_install('>= 1.1')").asInstanceOf[RubyObject]
      return true
    } catch {
      case exception: ClassCastException => {
        log.debug(exception.getMessage)
        return false
      }
    }
  }

  private def handlerNotReadyJet(any: Any) = {
    scheduleOnceRetry(5 seconds, sender(), any)
  }

  private def scheduleOnceRetry(delay: FiniteDuration, receive: ActorRef, message: Any) = {
    context.system.scheduler.scheduleOnce(delay, receive, message)(context.dispatcher, self)
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    globalStatusActor ! DeregisterNodeMonitorActor(self)
    log.debug(s"goodbye from ${self.path.name}")
  }
}
