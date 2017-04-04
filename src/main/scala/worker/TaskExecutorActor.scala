package worker

import Exceptions.{TestFailException, TestSuccessException}
import worker.messages.ExecuteTask

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
      val loader = new OwnLoader
      val cls : Class[_] = loader.getClassObject(msg.task.classname, msg.task.raw_cls)
      val obj = cls.newInstance()
      val method = cls.getMethod(msg.task.method)
      val res = method.invoke(obj)
      throw new TestSuccessException(msg.task, res)
    } catch {
      case e : Exception => {
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw new TestFailException(msg.task, e)
      }
    }

  }
}

class OwnLoader extends ClassLoader {

  def getClassObject(classname : String, raw_class : Array[Byte]) = {
    defineClass(classname, raw_class, 0, raw_class.length)
  }
}
