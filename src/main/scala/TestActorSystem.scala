import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import clustering.ClusteringTask
import communication.{HttpRequest, HttpResponse, ProxyRequest}
import org.apache.http.impl.client.HttpClientBuilder


class TaskExecutorActor extends Actor {
  val vmProxyActor : ActorRef = context.actorOf(Props[VMProxyActor], name="vmProxyActor")

  def receive = {
    case obj : Object =>
      for (interface <- obj.getClass.getInterfaces) {
        if (interface.getTypeName eq classOf[ClusteringTask].getTypeName) {
          for (field <- interface.getDeclaredFields) {
            if (field.getType.isAssignableFrom(classOf[ProxyRequest[Object]])) {
              field.setAccessible(true)
              val proxyRequest : ProxyRequest[Object] = field.get(obj).asInstanceOf[ProxyRequest[Object]]
              for (field <- proxyRequest.getClass.getDeclaredFields) {
                field.getName match {
                  case "vmProxy" =>
                    field.setAccessible(true)
                    field.set(proxyRequest, vmProxyActor)
                    assert(field.get(proxyRequest) eq vmProxyActor, "vm proxy injection failed.")
                  case _ =>
                }
              }
            }
          }
        }
      }
      for (method <- obj.getClass.getDeclaredMethods) {
        method.getName match {
          case name if name startsWith "test" =>
            method.invoke(obj)
          case _ =>
        }
      }
    case _ =>
  }
}

class VMProxyActor extends Actor {
  def receive = {
    case s : String =>
      sender() ! s"got a String : $s"
    case d : Integer =>
      sender() ! s"got an Integer : $d"
    case request : HttpRequest =>
      val client = HttpClientBuilder.create.build
      val response = client.execute(request.asInstanceOf[HttpRequest].getRequest)
      val output = new HttpResponse(response)
      sender() ! output
    case o =>
      sender() ! s"got an Object of class : ${o.getClass.getName}"
  }
}


object TestActorSystem extends App {
  val system = ActorSystem("testActorSystem")
  val executor = system.actorOf(Props[TaskExecutorActor], name="testActor")

  val test : TestEnvironment = new TestEnvironment()
  executor ! test

  system.terminate()
}