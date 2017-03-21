import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class Test4 extends ITest1 with ITest2 with ITest3 {
  override def test1() = { println("test1") }
  override def test2() = { println("test2") }
  override def test3() = { println("test3") }
}

class TestActor extends Actor {
  def receive = {
    case s : String =>
      println(s)
    case task : java.lang.reflect.Method =>
      task
  }
}

object TestActorSystem extends App {
  val system = ActorSystem("testActorSystem")
  val testActor = system.actorOf(Props[TestActor], name="testActor")

  testActor ! AnnotationTest.testCreateFile()
}
