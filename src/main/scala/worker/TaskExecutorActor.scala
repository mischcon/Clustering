package worker

import Exceptions.{TestFailException, TestSuccessException}
import clustering.ClusteringTask
import communication.ProxyRequest
import de.oth.clustering.java.TestingCodebaseLoader
import org.junit.Test
import org.junit.runner.{JUnitCore, Request, Result}
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
      val loader = new TestingCodebaseLoader()
      val cls : Class[_] = loader.getClassFromByte(msg.task.raw_cls, msg.task.classname)
      val obj = cls.newInstance()
      for (interface <- obj.getClass.getInterfaces){
        if(interface.getTypeName eq classOf[ClusteringTask].getTypeName){
          for(field <- interface.getDeclaredFields){
            if(field.getType.isAssignableFrom(classOf[ProxyRequest[Object]])) {
              field.setAccessible(true)
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
      val method = obj.getClass.getMethod(msg.task.method)
      val an = method.getAnnotation(classOf[Test])
      if (an != null) {
        println(s"JUnit test method found\ninvoking (method is: ${method}")
        val result : Result = new JUnitCore().run(Request.method(cls, method.getName))
        if (result.wasSuccessful) {
          throw new TestSuccessException(msg.task, result.toString)
        }
        else {
          throw new TestFailException(msg.task, result.getFailures.get(0).getException)
        }
      }
      else {
        println(s"invoking (method is: ${method}")
        val res = method.invoke(obj)
        throw new TestSuccessException(msg.task, res)
      }
    } catch {
      case e : Exception => {
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw new TestFailException(msg.task, e.getCause)
      }
    }

  }
}
