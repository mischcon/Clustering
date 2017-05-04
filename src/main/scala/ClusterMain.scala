import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import akka.cluster.Cluster
import clustering.ClusterType
import com.typesafe.config.ConfigFactory
import de.oth.clustering.java._
import utils._
import utils.db.{CreateTask, DBActor}
import vm.messages._
import vm.NodeMasterActor
import webui.ClusteringApi
import worker.InstanceActor
import worker.messages.{AddTask, Task}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import scala.io.StdIn

object ClusterMain extends App{

  val parser : ClusterOptionParser = new ClusterOptionParser()
  parser.parser.parse(args, Config()) match {
    case Some(cli_config) => {

      var config = ConfigFactory.load()

      // logging
      if(cli_config.verbose)
        config = ConfigFactory.parseString("akka.loglevel = INFO").withFallback(config)
      if(cli_config.debug)
        config = ConfigFactory.parseString("akka.loglevel = DEBUG").withFallback(config)


      // network interface for listening
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
          .withFallback(config.getConfig("master")
            .withFallback(ConfigFactory.parseString("akka.cluster.roles = [master, vm, executor]"))
            .withFallback(config)))

        Cluster(system).join(Address("akka.tcp", "the-cluster", localIp, 2550))
        println(s"Cluster created! Seed node IP is $localIp")

        val instanceActor : ActorRef = system.actorOf(Props[InstanceActor], "instances")
        val directory : ActorRef = system.actorOf(Props[ExecutorDirectoryServiceActor], "ExecutorDirectory")
        val dBActor : ActorRef = system.actorOf(Props[DBActor], "db")
        val apiActor : ActorRef = system.actorOf(Props(classOf[ClusteringApi], localIp), "api")
        val globalStatus : ActorRef = system.actorOf(Props[GlobalStatusActor], "globalStatus")
        val nodeMasterActor : ActorRef = system.actorOf(Props[NodeMasterActor], "nodeMasterActor")
        nodeMasterActor ! IncludeNode(Cluster(system).selfAddress)


        // Load codebase
        if(cli_config.input != null) {
          val loader: TestingCodebaseLoader = new TestingCodebaseLoader(cli_config.input)
          val testMethods = loader.getClassClusterMethods
          val version = loader.getVmConfig
          val datestring = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())
          // Add Tasks
            for (a <- testMethods.asScala.toList) {
              println(s"adding task ${a.classname}.${a.methodname} to table $datestring")
              var singleInstance: Boolean = true
              if (a.annotation.clusterType() == ClusterType.GROUPING)
                singleInstance = false

              // Add Task to dependency tree
              instanceActor ! AddTask(datestring, a.annotation.members().toList, Task(loader.getRawTestClass(a.classname), a.classname, a.methodname, singleInstance), version)

              // Add Task to Database
              dBActor ! CreateTask(s"${a.classname}.${a.methodname}", datestring)
            }
        }

        Thread.sleep(500)
        if(cli_config.debug)
          println(new PrivateMethodExposer(system)('printTree)())

        println("press key as soon as client has joined")
        StdIn.readLine()

        //testVMNodesActor ! "get"

        println("Press any key to stop...")
        StdIn.readLine()
        println("Shutting down the Cluster...")
        if(cli_config.debug)
          println(new PrivateMethodExposer(system)('printTree)())
        Await.result(system.terminate(), Duration.Inf)
        System.exit(0)
      }
      // CLIENT
      else {
        config = hostnameConfig.withFallback(config.getConfig("client").withFallback(config))

        var roles : List[String] = List("executor", "vm")

        if(!cli_config.withExecutor)
          roles = roles.filter(x => x != "executor")

        if(!cli_config.withVm)
          roles = roles.filter(x => x != "vm")

        if(roles.nonEmpty) {
          println(s"Starting client with roles: ${roles.mkString(",")}")
          config = ConfigFactory.parseString(s"akka.cluster.roles = [${roles.mkString(",")}]").withFallback(config)
        }

        val system : ActorSystem = ActorSystem("the-cluster", config)

        if(cli_config.seednode != null) {
          println(s"using ${cli_config.seednode} as seed-node")
          Cluster(system).join(Address("akka.tcp", "the-cluster", cli_config.seednode.split(":").head, cli_config.seednode.split(":").reverse.head.toInt))
        }

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
