package worker
import java.lang.reflect.Method

import Exceptions.{TestFailException, TestSuccessException}
import worker.messages.ExecuteTask

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
    log.debug(s"EXECUTING ${msg.task.method}")
    try {
      val cls : Class[_] = msg.task.cls
      val obj = cls.newInstance()
      val method = cls.getMethod(msg.task.method)
      val res = method.invoke(obj)
      throw TestSuccessException(msg.task, res)
    } catch {
      case e : Exception => {
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw new TestFailException(msg.task, e)
      }
    }

  }
}
