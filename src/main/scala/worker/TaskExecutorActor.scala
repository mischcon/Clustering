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
    log.debug("EXECUTING task + \"excepting\" result to parent / supervisor")
    try {
      /*
      * TODO search codebase for method name and execute
      * */
      println(s"EXECUTING METHOD: ${msg.task.method}")
      //TESTING
      if(msg.task.method.contains("fail"))
        throw new Exception("method failed")
    } catch {
      case e : Exception => {
        log.debug(s"invocation failed - ${e.getCause.toString}")
        throw new TestFailException(msg.task, e.getCause)
      }
    }
    throw TestSuccessException(msg.task, null)
  }
}
