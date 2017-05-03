package worker

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import vm.vagrant.configuration.VagrantEnvironmentConfig
import worker.messages._

/**
  * Actor that represents an instance of a task-run.
  * Every added jar file has its own InstanceActor
  */
class InstanceActor extends Actor with ActorLogging{

  // InstanceID + Ref of child + Version + VagrantEnvironmentConfig
  var instances : List[(String, ActorRef, String, VagrantEnvironmentConfig)] = List.empty

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
    case t : Terminated => instances = instances.filter(a => a._2 != t.actor)
  }

  def handleAddTask(msg : AddTask): Unit ={
    log.debug("add task")
    context.child(msg.instanceId) match {
      case Some(child) => child ! msg
      case None => {
        val ref = context.actorOf(Props(classOf[DistributorActor]), msg.instanceId)
        instances = (msg.instanceId, ref, msg.version.version(), msg.version) :: instances
        ref ! msg

        context.watch(ref)
      }
    }
  }

  def handleGetTask(msg : GetTask): Unit = {
    log.debug(s"received GetTask: $msg")
    if(!instances.exists(a => a._3 == msg.version.version())){
      log.debug("No more tasks available")
      sender() ! NoMoreTasks
    } else {
      instances.filter(a => a._3 == msg.version.version()).sortBy(a => a._1).head._2 forward msg
    }
  }

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
