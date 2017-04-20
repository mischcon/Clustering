import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import clustering.ClusteringTask
import communication._
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.runner.{JUnitCore, Request, Result}


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
  def sendRequest(httpRequest: HttpRequest) = {
    val client = HttpClientBuilder.create.build
    val response = client.execute(httpRequest.getRequest)
    val output = new RestApiResponse(response)
    sender() ! output
    response.close()
    client.close()
  }

  def receive = {
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
}


object TestActorSystem extends App {
  val system = ActorSystem("testActorSystem")
  val executor = system.actorOf(Props[TaskExecutorActor], name="testActor")

  // val test : Tests = new Tests()
  // executor ! test

  val result : Result = new JUnitCore().run(Request.method(classOf[JUnitTests], "testGetSuccess"))
  if (result.wasSuccessful)
    println("Test successful")
  else
    println("Test failed : " + result.getFailures)

  system.terminate()
}