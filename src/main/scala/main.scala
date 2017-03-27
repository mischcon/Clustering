import java.lang.reflect.Method

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import worker.{DistributorActor, TaskActor, TestVMNodesActor}
import worker.messages.{AddTask, GetTask, Task}

import scala.io.StdIn

/**
  * Created by mischcon on 3/20/17.
  */
object main extends App{

  val config = ConfigFactory.load()

  val system : ActorSystem = ActorSystem("the-cluster", config.getConfig("master").withFallback(config))
  println("hello from master!")

  val workerActor : ActorRef = system.actorOf(Props[DistributorActor], "distributor")

  var tc : TestClass = new TestClass()
  var method : Method = tc.getTestMethod

  val task = Task(method, false)

  workerActor ! AddTask(List("nodes"), task)
  workerActor ! AddTask(List("nodes"), task)
  workerActor ! AddTask(List("nodes", "rooms"), task)

  val testVMNodesActor : ActorRef = system.actorOf(Props(classOf[TestVMNodesActor], null), "vmActor")



  Thread.sleep(100)
  testVMNodesActor ! "get"
  Thread.sleep(10000)
  testVMNodesActor ! "get"

  println(new PrivateMethodExposer(system)('printTree)())

  StdIn.readLine()
  system.terminate()
}

class PrivateMethodCaller(x: AnyRef, methodName: String) {
  def apply(_args: Any*): Any = {
    val args = _args.map(_.asInstanceOf[AnyRef])

    def _parents: Stream[Class[_]] = Stream(x.getClass) #::: _parents.map(_.getSuperclass)

    val parents = _parents.takeWhile(_ != null).toList
    val methods = parents.flatMap(_.getDeclaredMethods)
    val method = methods.find(_.getName == methodName).getOrElse(throw new IllegalArgumentException("Method " + methodName + " not found"))
    method.setAccessible(true)
    method.invoke(x, args: _*)
  }
}

class PrivateMethodExposer(x: AnyRef) {
  def apply(method: scala.Symbol): PrivateMethodCaller = new PrivateMethodCaller(x, method.name)
}
