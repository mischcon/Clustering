import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class TestActor extends Actor {
  def receive = {
    case s : String           => println(s)
    case task : java.lang.reflect.Method =>
      task
  }
}

object TestActorSystem extends App {
  val system = ActorSystem("testActorSystem")
  val testActor = system.actorOf(Props[TestActor], name="testActor")

  testActor ! AnnotationTest.testCreateFile()
}
