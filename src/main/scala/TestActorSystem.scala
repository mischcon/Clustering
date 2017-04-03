import java.lang.reflect.{Method}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


class TaskExecutorActor extends Actor {
  val vmProxyActor : ActorRef = context.actorOf(Props[VMProxyActor], name="vmProxyActor")

  def receive = {
    case obj : Object =>
      for (field <- obj.getClass.getDeclaredFields) {
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
    case o =>
      sender() ! s"got an Object of class : ${o.getClass.getName}"
  }
}


object TestActorSystem extends App {
  val system = ActorSystem("testActorSystem")
  val executor = system.actorOf(Props[TaskExecutorActor], name="testActor")

  executor ! new AnnotationTest()

  system.terminate()
}