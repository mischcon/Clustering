import java.net.NetworkInterface

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory
import utils.db.{CreateTask, DBActor}
import utils.{ClusterOptionParser, Config, ExecutorDirectoryServiceActor}
import worker.messages.{AddTask, Task}
import worker.{DistributorActor, TestVMNodesActor}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object ClusterMain extends App{

  val parser : ClusterOptionParser = new ClusterOptionParser()
  parser.parser.parse(args, Config()) match {
    case Some(cli_config) => {

      val config = ConfigFactory.load()
      val interfaces = NetworkInterface.getNetworkInterfaces
      println("Choose ip for listening:")
      var counter = 0
      var ips_list : List[String] = List.empty
      while(interfaces.hasMoreElements) {
        val addresses = interfaces.nextElement().getInetAddresses
        while(addresses.hasMoreElements) {
          var ip = addresses.nextElement().getHostAddress
          println(s"  [$counter] $ip")
          counter += 1
          ips_list = ip :: ips_list
        }
      }
      var localIp = ips_list.reverse(StdIn.readInt())
      val hostnameConfig = ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname = $localIp")

      // MASTER
      if (cli_config.mode == "master") {
        val system : ActorSystem = ActorSystem("the-cluster", hostnameConfig
          .withFallback(config.getConfig("master").withFallback(config)))

        Cluster(system).join(Address("akka.tcp", "the-cluster", localIp, 2550))
        println(s"Cluster created! Seed node IP is $localIp")

        val distributorActor : ActorRef = system.actorOf(Props[DistributorActor], "distributor")
        val directory : ActorRef = system.actorOf(Props[ExecutorDirectoryServiceActor], "ExecutorDirectory")
        val dBActor : ActorRef = system.actorOf(Props[DBActor], "db")

        // TODO: Create missing / not yet implemented Actors

        // Load codebase
        val loader : TestingCodebaseLoader = new TestingCodebaseLoader(cli_config.input)
        val testMethods = loader.getClassClusterMethods

        // Add Tasks
        for(a <- testMethods.asScala.toList){
          var singleInstance : Boolean = true
          if(a.annotation.clusterType() == ClusterType.GROUPING)
            singleInstance = false

          // Add Task to dependency tree
          distributorActor ! AddTask(a.annotation.members().toList, Task(loader.getRawTestClass(a.classname), a.classname, a.methodname, singleInstance))

          // Add Task to Database
          dBActor ! CreateTask(s"${a.classname}.${a.methodname}")
        }

        /* TEST PURPOSE - REMOVE IF NOT NEEDED */

//        var tc : TestClass = new TestClass()
//
//        var method_success : Method = tc.getTestMethodSuccess
//        var method_fail : Method = tc.getTestMethodFail
//
//        val task_success = Task(method_success.getName, false)
//        val task_fail = Task(method_fail.getName, false)
//
//        distributorActor ! AddTask(List("nodes"), task_success)
//        distributorActor ! AddTask(List("nodes"), task_success)
//        distributorActor ! AddTask(List("nodes", "rooms"), task_success)
//        distributorActor ! AddTask(List("nodes", "rooms", "files"), task_fail)
//        distributorActor ! AddTask(List("groups"), task_success)
//        distributorActor ! AddTask(List("groups", "users"), task_fail)

        val testVMNodesActor : ActorRef = system.actorOf(Props(classOf[TestVMNodesActor], null), "vmActor")

        Thread.sleep(500)
        println(new PrivateMethodExposer(system)('printTree)())

        println("press key as soon as client has joined")
        StdIn.readLine()

        testVMNodesActor ! "get"

        /* END TEST PURPOSE */

        println("Press any key to stop...")
        StdIn.readLine()
        println("Shutting down the Cluster...")
        println(new PrivateMethodExposer(system)('printTree)())
        Await.result(system.terminate(), Duration.Inf)
        System.exit(0)
      }
      // CLIENT
      else {
        val system : ActorSystem = ActorSystem("the-cluster", hostnameConfig
          .withFallback(config.getConfig("client").withFallback(config)))

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
