import java.lang.reflect.{Method}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


class TestActor extends Actor {
  val vmProxyActor : ActorRef = context.actorOf(Props[VMProxyActor], name="vmProxyActor")

  def receive = {
    case task : Method =>
//      for (taskClassAttribute <- task.getDeclaringClass.getDeclaredFields) {
//        if (taskClassAttribute.getType.getName.equals("HttpRequest")) {
//          for (httpRequestField <- taskClassAttribute.getType.getDeclaredFields) {
//            httpRequestField.setAccessible(true)
//            val field : Field = httpRequestField;
//            if (f.isAnnotationPresent(classOf[PostInject])) {
//              f.getName match {
//                case "vmProxy" =>
//                  println(field.get("vmProxy"))
//                case "sender" =>
//                  println(field.get("sender"))
//              }
//            }
//          }
//        }
//      }
    case obj : Object =>
      println("HELLO")
      for (field <- obj.getClass.getDeclaredFields) {
        if (field.getType.getName.equals("HttpRequest")) {
          val httpRequest = field
          for (field <- field.getType.getDeclaredFields) {
            println("field: " + field.getName)
            println("value: " + field.get(obj))
          }
        }
      }
  }
}

class VMProxyActor extends Actor {
  def receive = {
    case s : String =>
      println(s)
  }
}


object TestActorSystem extends App {
  val system = ActorSystem("testActorSystem")
  val testActor = system.actorOf(Props[TestActor], name="testActor")

  val test : AnnotationTest = new AnnotationTest()

  testActor ! test

  system.terminate()
}