package vm

import java.net.URI
import javafx.scene.control.TreeTableView.ResizeFeatures

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import communication.{DeleteRequest, HttpRequest, HttpResponse, RequestMethod}
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
  }

}
