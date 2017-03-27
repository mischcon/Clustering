package worker
import java.lang.Exception

import Exceptions.{TestFailException, TestSuccessException}
import akka.actor.Actor.Receive
import worker.messages.{Result, Task}

/**
  * Created by mischcon on 26.03.2017.
  */
class TaskExecutorActor(task : Task, vmInfo : Object) extends WorkerTrait{

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case "run" => run()
  }

  def run(): Unit ={
    log.debug("EXECUTING task + \"excepting\" result to parent / supervisor")
    try {
      var obj = task.method.getDeclaringClass.newInstance()
      log.debug("created instance")
      task.method.invoke(obj)
      log.debug("invocation was successful")
    } catch {
      case e : Exception => {
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw new TestFailException(task, e.getCause)
      }
    }
    throw TestSuccessException(task, null)
  }
}
