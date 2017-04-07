package worker

import Exceptions.{TestFailException, TestSuccessException}
import clustering.ClusteringTask
import communication.ProxyRequest
import worker.messages.ExecuteTask

class TaskExecutorActor extends WorkerTrait{

  override def preStart(): Unit = {
    super.preStart()
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug(s"goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case t : ExecuteTask => run(t)
    case a => log.warning(s"received unexpected message: $a")
  }

  def run(msg : ExecuteTask): Unit ={
    log.debug(s"EXECUTING ${msg.task.method}")
    try {
      val loader = new OwnLoader
      val cls : Class[_] = loader.getClassObject(msg.task.classname, msg.task.raw_cls)
      val obj = cls.newInstance()
      println("got object")
      for (interface <- obj.getClass.getInterfaces){
        println("got interface")
        if(interface.getTypeName eq classOf[ClusteringTask].getTypeName){
          for(field <- interface.getDeclaredFields){
            println("got field")
            if(field.getType.isAssignableFrom(classOf[ProxyRequest[Object]])) {
              field.setAccessible(true)
              println("set acccessible")
              val proxyRequest : ProxyRequest[Object] = field.get(obj).asInstanceOf[ProxyRequest[Object]]
              for (field <- proxyRequest.getClass.getDeclaredFields) {
                field.getName match {
                  case "vmProxy" =>
                    field.setAccessible(true)
                    field.set(proxyRequest, msg.targetVM)
                    assert(field.get(proxyRequest) eq msg.targetVM, "vm proxy injection failed.")
                  case _ =>
                }
              }
            }
          }
        }
      }
      println("getting method")
      val method = obj.getClass.getMethod(msg.task.method)
      println(s"invoking (method is: ${method}")
      val res = method.invoke(obj)
      throw new TestSuccessException(msg.task, res)
    } catch {
      case e : Exception => {
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw new TestFailException(msg.task, e.getCause)
      }
    }

  }
}

class OwnLoader extends ClassLoader {

  def getClassObject(classname : String, raw_class : Array[Byte]) = {
    defineClass(classname, raw_class, 0, raw_class.length)
  }
}
