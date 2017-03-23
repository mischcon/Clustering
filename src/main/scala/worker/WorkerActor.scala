package worker
import akka.actor.{Actor, Props}
import akka.remote.ContainerFormats.ActorRef
import worker.messages.{AddTask, GetTask}

class WorkerActor extends Actor{

  println(self.path.name + " was created!")

  override def receive: Receive = {
    case p : AddTask => addTask(p)
    case p : GetTask => getTask(p)
  }

  def addTask(msg : AddTask) = {
    val api = msg.group.head
    context.child(api) match {
      case Some(child) => child ! msg
      case None => {
        msg.task.singleInstance match {
          case false => context.actorOf(Props(classOf[GroupActor], msg.group.take(1)), api) ! msg
          case true => context.actorOf(Props(classOf[SingleInstanceActor], msg.group.take(1)), api) ! msg
        }
      }
    }
  }

  def getTask(msg : GetTask) = {
    context.children.head forward msg
  }
}
