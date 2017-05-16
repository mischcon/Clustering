package de.oth.clustering.scala.worker

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import de.oth.clustering.scala.utils.DeployInfoInterface
import de.oth.clustering.scala.vm.messages.{GetInstanceActor, SetInstanceActor}
import de.oth.clustering.scala.vm.vagrant.configuration.VagrantEnvironmentConfig
import de.oth.clustering.scala.worker.messages._

/**
  * This Actor keeps track of all uploaded task runs / .jar files.
  * For every uploaded file it creates a new  {@link de.oth.clustering.scala.worker#DistributorActor DistributorActor}.
  * If an {@link de.oth.clustering.scala.vm#VMProxyActor VMProxyActor} requests a new task than it sends its request together with information about what
  * version is deployed on the VM - the InstanceActor then searches for a suitable instance
  * (one with a matching version) an forwards the request.
  */
class InstanceActor extends Actor with ActorLogging {

  // InstanceID + Ref of child + Version + DeployInfoInterface
  var instances : List[(String, ActorRef, String, DeployInfoInterface)] = List.empty

  override def preStart(): Unit = {
    log.debug(s"Hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"Goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case p : AddTask => handleAddTask(p)
    case p : GetTask => handleGetTask(p)
    case GetDeployInfo => handleGetDeployInfo()
    case t : Terminated => handleRunComplete(t.actor); instances = instances.filter(a => a._2 != t.actor)
    case GetInstanceActor => sender() ! SetInstanceActor(self)
  }

  /**
    * This function will be called every time a task run is completed.
    * With the help of the instance list the task run can be identified.
    * @param ref ActorRef of terminated DistributorActor
    */
  def handleRunComplete(ref : ActorRef) : Unit = {
    val done = instances.filter(a => a._2 == ref).head
    println(s"Run ${done._1} with version ${done._3} done!")
    if(instances.length == 1)
      println("No more instances present - upload new tasks or shutdown the cluster!")

    /*
    * Enter additional code that should be executed once a run is complete here
    * */
  }

  def getTableNameByActorRef(ref : ActorRef) : String = {
    for(a <- instances){
      if(a._2 == ref)
        return a._1
    }
    return null
  }

  /**
    * Creates a DistributorActor (if there are no suitable) and forwards incoming
    * {@link de.oth.clustering.scala.worker.messages#AddTask AddTask} messages to it
    * @param msg
    */
  def handleAddTask(msg : AddTask): Unit ={
    log.debug("add task")
    context.child(msg.instanceId) match {
      case Some(child) => child ! msg
      case None => {
        val ref = context.actorOf(Props(classOf[DistributorActor]), msg.instanceId)
        instances = (msg.instanceId, ref, msg.version.version(), msg.version) :: instances
        log.debug(s"added task: ${msg.instanceId}, $ref, ${msg.version.version()}, ${msg.version}")
        ref ! msg

        context.watch(ref)
      }
    }
  }

  /**
    * Forwards {@link de.oth.clustering.scala.worker.messages#GetTask GetTask} messages to all its children with a suitable version.
    * If no suitable child was found, then a {@link de.oth.clustering.scala.worker.messages#NoMoreTasks NoMoreTasks} message is sent as reply to the request.
    * @param msg
    */
  def handleGetTask(msg : GetTask): Unit = {
    log.debug(s"received GetTask: $msg")
    if(!instances.exists(a => a._3 == msg.version)){
      log.debug("No more tasks available")
      sender() ! NoMoreTasks
    } else {
      instances.filter(a => a._3 == msg.version).sortBy(a => a._1).head._2 forward msg
    }
  }

  /**
    * Sends the to-be-deployed version of a task run to the {@link de.oth.clustering.scala.vm#VMActor VMActor}.
    * If there are no task run instances it replies with a {@link de.oth.clustering.scala.worker.messages#NoDeployInfo NoDeployInfo} message.
    */
  def handleGetDeployInfo() : Unit = {
    log.debug("GetDeployInfo called")
    if(instances.nonEmpty){
      val info = instances.sortBy(a => a._1).head._4
      log.debug(s"sending DeployInfo: $info")
      sender() ! DeployInfo(info)
    } else {
      sender() ! NoDeployInfo
    }
  }
}
