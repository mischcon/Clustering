package worker

import Exceptions.{TestFailException, TestSuccessException}
import clustering.ClusteringTask
import communication.ProxyRequest
import de.oth.clustering.java.TestingCodebaseLoader
import org.junit.Test
import org.junit.runner.{JUnitCore, Request, Result}
import worker.messages.ExecuteTask
import worker.traits.WorkerTrait

/**
  * Actor responsible for executing tasks. Is created by a TaskActor and is being supervised / watched by
  * the creating TaskActor and the task target (VMProxyActor)
  */
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

  /**
    * A task and a target are passed to this function.
    * The actual task class is loaded through a classloader and is being analyzed -
    * if the class implements the ClusteringTask interface then the target is being injected
    * into the object, resulting in the task to sent all its requests to the target rather than
    * performing them locally.
    *
    * The method also checks whether or not the task is a JUnit test (@Test annotation) -
    * in this case the task is not simply being executed but passed to a JUnitCore testrunner.
    *
    * In case the execution of the task was successfully a TestSuccessException /
    * unsuccessfully a TestFailException will be thrown and passed
    * to the parent actor (TaskActor).
    *
    * @param msg ExecuteTask message containing the task and the target
    */
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
        log.debug(s"JUnit test method found\ninvoking (method is: $method")
        val result : Result = new JUnitCore().run(Request.method(cls, method.getName))
        if (result.wasSuccessful()) {
          if (result.getIgnoreCount == 1)
            throw TestSuccessException(msg.task, "IGNORE")
          else
            throw TestSuccessException(msg.task, "SUCCESS")
        }
        else
          throw TestFailException(msg.task, result.getFailures.get(0).getException)
      }
      else {
        log.debug(s"invoking (method is: $method")
        val res = method.invoke(obj)
        throw TestSuccessException(msg.task, res)
      }
    } catch {
      case e : Exception =>
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw TestFailException(msg.task, e.getCause)
    }

  }
}
