package worker

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.actor.Status.Failure
import akka.pattern._
import akka.contrib.pattern.Aggregator
import akka.util.Timeout

import scala.concurrent.duration._
import worker.messages.{GetTask, SendTask, Task, TimedOut}

import scala.concurrent.ExecutionContext

class TaskAggregator(candidateSequence : Seq[ActorRef], timeout : FiniteDuration) extends Actor with Aggregator with ActorLogging{

  implicit val ec : ExecutionContext = ExecutionContext.Implicits.global

  override def preStart(): Unit = {
    log.debug(s"Hello from ${self.path.name}\nmy candidates are: ${candidateSequence}")
  }

  override def postStop(): Unit = {
    log.debug(s"Goodbye from ${self.path.name}")
  }

  expectOnce {
    case GetTask(version) => log.debug(s"${self.path.name} original sender: ${sender().path.toString}"); new MultiResponseHandler(sender(), version)
  }

  class MultiResponseHandler(originalSender : ActorRef, version : String) {
    candidateSequence.foreach(x => x ! GetTask(version))

    var taskSenderMap : Map[ActorRef, List[SendTask]] = Map.empty

    context.system.scheduler.scheduleOnce(timeout, self, TimedOut)

    val handle = expect {
      case SendTask(task : Task, source : ActorRef) => {
        log.debug(s"${self.path.name} received SendTask from ${source.path}")
        var l = taskSenderMap.getOrElse(sender(), Nil)
        l = SendTask(task, source) :: l
        taskSenderMap += (source -> l)

        // if the first member of the candidate sequence has tasks, abort
        if(source == candidateSequence.head)
          process()
      }
      case TimedOut => process()
    }

    def process(): Unit = {
      log.debug(s"${self.path.name} taskSenderMap size: ${taskSenderMap.size}")
      for(a <- taskSenderMap)
        log.debug(s"${self.path.name}  tsm: " + a._1.path.toString + " | " +  a._2)
      log.debug(s"${self.path.name} candidateSequence")
      for(a <- candidateSequence)
        log.debug(s"${self.path.name}  cs: " + a.path.toString)
      unexpect(handle)
      val handle_rejection = expect {
        case SendTask(task : Task, source : ActorRef) => source ! Failure(new Exception)
      }

      var task_provided = false
      for(a <- candidateSequence){
        val x = taskSenderMap.filterKeys(t => t.path.toString.contains(a.path.toString)).head._1
        taskSenderMap.get(x) match {
          case Some(list) => {
            if(task_provided)
              a ! Failure(new Exception)
            else {
              log.debug(s"${self.path.name} asking ${originalSender.path} for SendTask with source ${list.head.source.path}")
              originalSender.ask(SendTask(list.head.task, list.head.source))(Timeout(1, TimeUnit.SECONDS), list.head.source)
              taskSenderMap += (a -> list.tail)
              task_provided = true
            }
          }
          case None => //ignore
        }
      }
      unexpect(handle_rejection)
      context.stop(self)
    }
  }
}
