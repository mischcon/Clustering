package worker

import akka.actor.Props
import akka.pattern.ask
import worker.messages.{AddTask, AddTaskRecovery, Task}
import java.lang.reflect.Method

import scala.util.{Failure, Success}

abstract class SubWorkerActor(var group : List[String]) extends WorkerTrait{

  var tasks = List[Task]

  override def receive: Receive = {
    case p : AddTask => addTask(p)
  }

  def addTask(msg : AddTask) = {
    // create new worker
    if(msg.group.length > group.length){
      val name = msg.group.take(group.length + 1)
      context.actorOf(Props(classOf[context.type], name), name.mkString(".")) ! msg
    }
    // or create an actual task worker, but do not yet start it
    else {
      /* M A G I C */
    }
  }

  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = {
    case p : AddTask => persist(AddTaskRecovery(p.group, p.task))(addTask)
  }

  override def persistenceId: String = self.path.name
}

class GroupActor(group : List[String]) extends SubWorkerActor(group) {
  print(self.path.name + " was created!")
}
class SingleInstanceActor(group : List[String]) extends SubWorkerActor(group) {
  print(self.path.name + " was created!")
}
