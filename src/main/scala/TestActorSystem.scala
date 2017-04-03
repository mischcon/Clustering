import java.lang.reflect.Method

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class TestActor extends Actor {
  def receive = {
    case obj : Object =>

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

  testActor ! test.testGetNodes()
  system.terminate()
}