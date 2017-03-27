package worker
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
    case "run" => {
      /* execute task */
      log.debug("EXECUTING task + \"excepting\" result to parent / supervisor")

      throw TestSuccessException(task, null)
    }
    case "run_fail" => {
      /* execute task */
      log.debug("FAILING task + \"excepting\" result to parent / supervisor")

      throw TestFailException(task, null)
    }
    case a => println(a)
  }
}
