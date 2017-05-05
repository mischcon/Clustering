package worker
import akka.actor.{Props, Terminated}
import worker.messages.{AddTask, GetTask}

/**
  * Sometimes tasks might affect other tasks (e.g. if one task changes a global configuration than the concurrent
  * execution of this task might affect other tasks that are being executed on the same machine), which is why a
  * separation between tasks that should be run on a SINGLE_INSTANCE and tasks that can safely be executed together
  * with other tasks in a GROUP is necessary. Every DistributorActor can have many instances of
  * {@link worker.SingleInstanceActor SingleInstanceActors} / {@link worker.GroupActor GroupActors} as its children. If a DistributorActor has no more children,
  * than this means that all tasks that belong to a specific task run have been processed -
  * in this case the actor kills itsself.
  */
class DistributorActor extends WorkerTrait{

  override def preStart(): Unit = {
    super.preStart()
    log.debug("hello from distributor!")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug("Goodbye from distributor!")
  }

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
    case t : Terminated => if(context.children.isEmpty) context.stop(self)
    case a => log.warning(s"received unexpected message: $a")
  }

  /**
    * Creates {@link worker.SingleInstanceActor SingleInstanceActors} / {@link worker.GroupActor GroupActors} (if there are no) and forwards incoming
    * {@link worker.messages#AddTask} messages to them
    * @param msg
    */
  def addTask(msg : AddTask) = {
    val api = msg.group.head
    context.child(api) match {
      case Some(child) => child ! msg
      case None => {
        msg.task.singleInstance match {
          case false => context.actorOf(Props(classOf[GroupActor], msg.group.take(1), self.path.name), api) ! msg
          case true => context.actorOf(Props(classOf[SingleInstanceActor], msg.group.take(1), self.path.name), api) ! msg
        }
      }
    }
    context.children.foreach(x => context.watch(x))
  }

  /**
    * Forwards {@link worker.messages#GetTask} messages to all its children.
    * If there are no more children left this actor will be killed.
    * @param msg
    */
  def getTask(msg : GetTask) = {
    if(context.children.isEmpty){
      log.debug("No children present - stopping self")
      context.stop(self)
    }
    log.debug(s"received getTask - forwarding (have children: ${context.children.size})")
    context.children.foreach(u => u forward msg)
  }
}
