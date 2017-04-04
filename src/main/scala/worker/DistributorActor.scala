package worker
import akka.actor.Props
import worker.messages.{AddTask, GetTask, NoMoreTasks}

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
    case a => log.warning(s"received unexpected message: $a")
  }

  def addTask(msg : AddTask) = {
    val api = msg.group.head
    context.child(api) match {
      case Some(child) => child ! msg
      case None => {
        msg.task.singleInstance match {
          case false => context.actorOf(Props(classOf[GroupActor], msg.group.take(1)), s"GROUP-$api") ! msg
          case true => context.actorOf(Props(classOf[SingleInstanceActor], msg.group.take(1)), s"SINGLEINSTANCE-$api") ! msg
        }
      }
    }
  }

  def getTask(msg : GetTask) = {
    if(context.children.isEmpty){
      log.debug("No children present")
      sender() ! NoMoreTasks
    }
    log.debug(s"received getTask - forwarding (have children: ${context.children.size})")
    context.children.foreach(u => u forward msg)
  }
}