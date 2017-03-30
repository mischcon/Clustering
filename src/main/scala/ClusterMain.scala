import java.lang.reflect.Method

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory
import utils.db.DBActor
import utils.{ClusterOptionParser, Config, ExecutorDirectoryServiceActor, PrivateMethodExposer}
import worker.{DistributorActor, TestVMNodesActor}
import worker.messages.{AddTask, Task}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object ClusterMain extends App{

  val parser : ClusterOptionParser = new ClusterOptionParser()
  parser.parser.parse(args, Config()) match {
    case Some(cli_config) => {

      val config = ConfigFactory.load()
      // MASTER
      if (cli_config.mode == "master") {
        val system : ActorSystem = ActorSystem("the-cluster", config.getConfig("master").withFallback(config))
        Cluster(system).join(Address("akka.tcp", "the-cluster", "localhost", 2550))

        val workerActor : ActorRef = system.actorOf(Props[DistributorActor], "distributor")
        val directory : ActorRef = system.actorOf(Props[ExecutorDirectoryServiceActor], "ExecutorDirectory")
        val dBActor : ActorRef = system.actorOf(Props[DBActor], "db")

        // TODO: Create missing / not yet implemented Actors

        // TODO: Load codebase

        // TODO: Add Tasks and start the execution

        /* TEST PURPOSE - REMOVE IF NOT NEEDED */

        var tc : TestClass = new TestClass()

        var method_success : Method = tc.getTestMethodSuccess
        var method_fail : Method = tc.getTestMethodFail

        val task_success = Task(method_success.getName, false)
        val task_fail = Task(method_fail.getName, false)

        workerActor ! AddTask(List("nodes"), task_success)
        workerActor ! AddTask(List("nodes"), task_success)
        workerActor ! AddTask(List("nodes", "rooms"), task_success)
        workerActor ! AddTask(List("nodes", "rooms", "files"), task_fail)
        workerActor ! AddTask(List("groups"), task_success)
        workerActor ! AddTask(List("groups", "users"), task_fail)

        val testVMNodesActor : ActorRef = system.actorOf(Props(classOf[TestVMNodesActor], null), "vmActor")

        Thread.sleep(500)
        println(new PrivateMethodExposer(system)('printTree)())

        println("press key as soon as client has joined")
        StdIn.readLine()

        testVMNodesActor ! "get"
        Thread.sleep(500)
        testVMNodesActor ! "get"
        Thread.sleep(500)
        testVMNodesActor ! "get"
        Thread.sleep(500)
        testVMNodesActor ! "get"
        Thread.sleep(500)
        testVMNodesActor ! "get"
        Thread.sleep(500)
        testVMNodesActor ! "get"
        Thread.sleep(500)

        println(new PrivateMethodExposer(system)('printTree)())

        /* END TEST PURPOSE */

        println("Press any key to stop...")
        StdIn.readLine()
        println("Shutting down the Cluster...")
        Await.result(system.terminate(), Duration.Inf)
        System.exit(0)
      }
      // CLIENT
      else {
        val system : ActorSystem = ActorSystem("the-cluster", config.getConfig("client").withFallback(config))

        if(cli_config.seednode != null) {
          println(s"using ${cli_config.seednode} as seed-node")
          Cluster(system).join(Address("akka.tcp", "the-cluster", cli_config.seednode.split(":").head, cli_config.seednode.split(":").reverse.head.toInt))
        }

        if(!cli_config.withExecutor)
          println("TODO: Inform the cluster that this client cannot provide executors")

        if(!cli_config.withVm)
          println("TODO: Inform the cluster that this client cannot provide VMs")

        println("Press any key to gracefully stop the client...")
        StdIn.readLine()
        println("Leaving the Cluster...")
        val cluster = Cluster(system)
        cluster.leave(cluster.selfAddress)
        println("Shutting down the client...")
        Await.result(system.terminate(), Duration.Inf)
        System.exit(0)
      }


    }
    case None =>
      System.exit(1)
  }
}
