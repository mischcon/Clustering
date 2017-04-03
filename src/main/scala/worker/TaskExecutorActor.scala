package worker



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
      println("Sleeping for 10 seconds...")
      Thread.sleep(10000)
      val loader = new OwnLoader
      val cls : Class[_] = loader.getClassObject(msg.task.classname, msg.task.raw_cls)
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

class OwnLoader extends ClassLoader {

  def getClassObject(classname : String, raw_class : Array[Byte]) = {
    defineClass(classname, raw_class, 0, raw_class.length)
  }
}
