package de.oth.clustering.scala.worker
import akka.actor.{ActorRef, Props, Terminated}
import de.oth.clustering.scala.worker.messages.{AddTask, GetTask, PersistAndSuicide}
import de.oth.clustering.scala.worker.traits.WorkerTrait

/**
  * Sometimes tasks might affect other tasks (e.g. if one task changes a global configuration than the concurrent
  * execution of this task might affect other tasks that are being executed on the same machine), which is why a
  * separation between tasks that should be run on a SINGLE_INSTANCE and tasks that can safely be executed together
  * with other tasks in a GROUP is necessary. Every DistributorActor can have many instances of
  * {@link de.oth.clustering.scala.worker.SingleInstanceActor SingleInstanceActors} / {@link de.oth.clustering.scala.worker.GroupActor GroupActors} as its children. If a DistributorActor has no more children,
  * than this means that all tasks that belong to a specific task run have been processed -
  * in this case the actor kills itsself.
  */
class DistributorActor extends WorkerTrait{

  private var singleInstanceList : List[ActorRef] = List.empty
  private var groupInstanceList : List[ActorRef] = List.empty

  override def preStart(): Unit = {
    super.preStart()
    log.debug("hello from distributor!")
  }

  override def postStop(): Unit = {
    // persists the current status
    context.children.foreach(x => x ! PersistAndSuicide)
    singleInstanceList = List.empty
    groupInstanceList = List.empty
    super.postStop()
    log.debug("Goodbye from distributor!")
  }

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
    case t : Terminated => handleTermianted(t)
    case a => log.warning(s"received unexpected message: $a")
  }

  /**
    * Creates {@link de.oth.clustering.scala.worker.SingleInstanceActor SingleInstanceActors} / {@link de.oth.clustering.scala.worker.GroupActor GroupActors} (if there are no) and forwards incoming
    * {@link de.oth.clustering.scala.worker.messages#AddTask} messages to them
    * @param msg
    */
  def addTask(msg : AddTask) = {
    val api = msg.group.head
    context.child(api) match {
      case Some(child) => child ! msg
      case None => {
        msg.task.singleInstance match {
          case false => {
            var ref = context.actorOf(Props(classOf[GroupActor], msg.group.take(1), self.path.name), api)
            groupInstanceList = ref :: groupInstanceList
            ref ! msg
          }
          case true => {
            var ref = context.actorOf(Props(classOf[SingleInstanceActor], msg.group.take(1), self.path.name), api)
            singleInstanceList = ref :: singleInstanceList
            ref ! msg
          }
        }
      }
    }
    context.children.foreach(x => context.watch(x))
  }

  /**
    * Handle the termination of a child actor
    * Remove the terminated actor from the list.
    * Commit suicide if there are no more children.
    * @param t
    */
  def handleTermianted(t : Terminated) = {
    if(context.children.isEmpty){
      context.stop(self)
    } else {
      singleInstanceList = singleInstanceList.filter(x => x != t.actor)
      groupInstanceList = groupInstanceList.filter(x => x != t.actor)
    }
  }

  /**
    * Forwards {@link de.oth.clustering.scala.worker.messages#GetTask} messages to its singleInstance / groupInstance / all children (depending on the parameter).
    * If there are no more children left this actor will be killed.
    * @param msg
    */
  def getTask(msg : GetTask) = {
    if (context.children.isEmpty) {
      log.debug("No children present - stopping self")
      context.stop(self)
    } else {
      // check single instance
      msg.singleInstance match {
        case true => {
          if(singleInstanceList.nonEmpty) {
            log.debug(s"received getTask with singleInstance: true - forwarding (have singleInstance: ${singleInstanceList.size})")
            singleInstanceList.foreach(u => u forward msg)
          } else {
            log.debug(s"received getTask with singleInstance: true, but we have no singleInstances - forwarding to all (have children: ${context.children.size})")
            context.children.foreach(u => u forward msg)
          }
        }
        case false => {
          if(groupInstanceList.nonEmpty) {
            log.debug(s"received getTask with singleInstance: false - forwarding (have groupInstance: ${groupInstanceList.size})")
            groupInstanceList.foreach(u => u forward msg)
          } else {
            log.debug(s"received getTask with singleInstance: false, but we have no groupInstance - forwarding to all (have children: ${context.children.size})")
            context.children.foreach(u => u forward msg)
          }
        }
        case _ => {
          log.debug(s"received getTask with singleInstance: null - forwarding (have children: ${context.children.size})")
          context.children.foreach(u => u forward msg)
        }
      }

    }
  }
}
