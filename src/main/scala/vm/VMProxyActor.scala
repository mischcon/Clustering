package vm

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, Terminated}
import communication.{HttpRequest, HttpResponse}
import org.apache.http.impl.client.HttpClientBuilder
import worker.messages._

/**
  * Created by mischcon on 3/20/17.
  */
class VMProxyActor extends Actor with ActorLogging{

  var haveSpaceForTasks = true

  //TODO: define what vmInfo is
  var vmInfo : Object = null


  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }

  override def receive: Receive = {
    case "get" => {
      log.debug("sent GetTask to distributor")
      context.system.actorSelection("/user/instances") ! GetTask()
    }
    case t : SendTask if haveSpaceForTasks => {
      log.debug("received SendTask and I still have space for tasks!")
      haveSpaceForTasks = false

      sender() ! AcquireExecutor(vmInfo, self)
    }
    case t : SendTask if ! haveSpaceForTasks => {
      log.debug("received SendTask but I dont have any more space :(")
      sender() ! Failure(new Exception)
    }
    case t : Executor => {
      log.debug("received an ActorRef, which means that this is an executor - monitoring it now")
      context.watch(t.ref)
    }
    case t : Terminated => {
      log.debug(s"received TERMINATED from ${t.actor.path.toString}, which means that the task is done - now I have space for a new task!")
      handleFailure()
    }
    case CannotGetExecutor => handleFailure()
    case s : String =>
      sender() ! s"got a String : $s"
    case d : Integer =>
      sender() ! s"got an Integer : $d"
    case request : HttpRequest =>
      log.debug("creating HttpClient")
      val client = HttpClientBuilder.create.build
      log.debug("getting response")
      val req = request.getRequest
      log.debug(s"request: $req")
      val response = client.execute(req)
      log.debug("parsing")
      val output = new HttpResponse(response)
      log.debug("sending")
      sender() ! output
    case o =>
      sender() ! s"got an Object of class : ${o.getClass.getName}"
  }

  def handleFailure(): Unit ={
    log.debug("releasing task - now I have space for a new task!")
    haveSpaceForTasks = true

    Thread.sleep(1000)
    self ! "get"
  }

}
