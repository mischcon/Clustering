import java.io.{BufferedReader, InputStreamReader}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import communication.{ClusteringTask, ProxyRequest}
import sun.net.www.protocol.https.HttpsURLConnectionImpl


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
      sender() ! s"got an Integer was sent : $d"
    case request : HttpsURLConnectionImpl =>
      val responseCode = request.getResponseCode
      println(s"sending '${request.getRequestMethod}' request to URL : ${request.getURL}")
      println(s"response code : $responseCode")
      var in : BufferedReader = null
      if (200 <= responseCode && responseCode <= 299)
        in = new BufferedReader(new InputStreamReader(request.getInputStream))
      else
        in = new BufferedReader(new InputStreamReader(request.getErrorStream))
      val output = Stream.continually(in.readLine()).takeWhile(_ != null).mkString("\n")
      in.close()
      sender() ! output
    case o =>
      sender() ! s"got an Object of class : ${o.getClass.getName}"
  }
}


object TestActorSystem extends App {
  val system = ActorSystem("testActorSystem")
  val executor = system.actorOf(Props[TaskExecutorActor], name="testActor")
  executor ! new AnnotationTest()

  val test : AnnotationTest = new AnnotationTest()
  test.testPost()

  system.terminate()
}