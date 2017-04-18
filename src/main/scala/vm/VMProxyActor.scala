package vm

<<<<<<< HEAD
import java.net.URI
import javafx.scene.control.TreeTableView.ResizeFeatures

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import communication.{DeleteRequest, HttpRequest, HttpResponse, RequestMethod}
=======
import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, Terminated}
import communication._
>>>>>>> 254af187e0bcf6c1ac5b2379fdfbbaf4ae91fda7
import org.apache.http.impl.client.HttpClientBuilder
import vm.vagrant.configuration.Service
import vm.vagrant.configuration.Service.Service

import scala.concurrent.Future
/**
  * Created by mischcon on 3/20/17.
  */
class VMProxyActor extends Actor with ActorLogging {
  var port: Int = 443
  var service: Service = Service.https
  var vmActor: ActorRef = _

  override def receive: Receive = {
<<<<<<< HEAD
    case request: HttpRequest => log.debug(s"Got HttpRequest"); sender() ! httpRequest(request)
    case actorRef: ActorRef => vmActor = actorRef
    case _ => sender() ! "Unknown communication type!!"
  }

  def httpRequest(httpRequest: HttpRequest): HttpResponse = {
    //ToDo: URL PORT anpassen
    var request = httpRequest.getRequest
    var url = httpRequest.getUrl
    var uri = new URI(url)
    var builder = new StringBuilder()
    builder.append(s"${service.toString}://git.pc-ziegert.de")
    if (uri.getQuery != null && !uri.getQuery.isEmpty) builder.append(s"?${uri.getQuery}")
    uri = URI.create(builder.toString())
    request.setURI(uri)
    val client = HttpClientBuilder.create.build
    val response = client.execute(httpRequest.asInstanceOf[HttpRequest].getRequest)
    log.debug(s"Send HttpResponse")
    new HttpResponse(response)
=======
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
    case request : RestApiRequest =>
      request.getMethod match {
        case "GET" =>
          val httpGet : GetRequest = new GetRequest(request)
          sendRequest(httpGet)
        case "POST" =>
          val httpPost : PostRequest = new PostRequest(request)
          sendRequest(httpPost)
        case "PUT" =>
          val httpPut : PutRequest = new PutRequest(request)
          sendRequest(httpPut)
        case "DELETE" =>
          val httpDelete : DeleteRequest = new DeleteRequest(request)
          sendRequest(httpDelete)
      }
    case o =>
      sender() ! s"got an Object of class : ${o.getClass.getName}"
  }

  def sendRequest(httpRequest: HttpRequest) = {
    log.debug("creating HttpClient")
    val client = HttpClientBuilder.create.build
    log.debug("getting response")
    val req = httpRequest.getRequest
    log.debug(s"request: ${httpRequest.getUrl}")
    val response = client.execute(req)
    log.debug("parsing")
    val output = new RestApiResponse(response)
    log.debug("sending")
    sender() ! output
    response.close()
    client.close()
  }

  def handleFailure(): Unit ={
    log.debug("releasing task - now I have space for a new task!")
    haveSpaceForTasks = true

    Thread.sleep(1000)
    self ! "get"
>>>>>>> 254af187e0bcf6c1ac5b2379fdfbbaf4ae91fda7
  }

}
