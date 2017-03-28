package worker
import java.lang.Exception

import Exceptions.{TestFailException, TestSuccessException}
import akka.actor.Actor.Receive
import worker.messages.{ExecuteTask, Result, Task}

/**
  * Created by mischcon on 26.03.2017.
  */
class TaskExecutorActor extends WorkerTrait{

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case t : ExecuteTask => run(t)
  }

  def run(msg : ExecuteTask): Unit ={
    log.debug("EXECUTING task + \"excepting\" result to parent / supervisor")
    try {
      var obj = msg.task.method.getDeclaringClass.newInstance()
      log.debug("created instance")
      msg.task.method.invoke(obj)
      log.debug("invocation was successful")
    } catch {
      case e : Exception => {
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw new TestFailException(msg.task, e.getCause)
      }
    }
    throw TestSuccessException(msg.task, null)
  }
}
