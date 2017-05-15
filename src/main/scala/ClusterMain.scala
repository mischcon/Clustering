import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import clustering.{ClusterType, TrafficLoad}
import com.typesafe.config.ConfigFactory
import de.oth.clustering.java._
import utils._
import utils.db._
import vm.NodeMasterActor
import vm.messages._
import webui.ClusteringApi
import worker.InstanceActor
import worker.messages.{AddTask, Task}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object ClusterMain extends App {

  implicit val timeout = Timeout(3 seconds)

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
      var hostnameConfig = ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname = $localIp")

      // port
      var port : Int = cli_config.port
      hostnameConfig = hostnameConfig.withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port"))

      // MASTER
      if (cli_config.mode == "master") {
        val system : ActorSystem = ActorSystem("the-cluster", hostnameConfig
          .withFallback(ConfigFactory.parseString("akka.cluster.roles = [master, vm, executor]"))
            .withFallback(config))

        val log = Logging.getLogger(system, this);
        Cluster(system).join(Address("akka.tcp", "the-cluster", localIp, 2550))
        println(s"Cluster created! Seed node IP is $localIp")

        var dbConf : com.typesafe.config.Config = null

        // Load db.conf
        if(cli_config.db != null) {
          dbConf = ConfigFactory.parseFile(cli_config.db)
        }

        val instanceActor : ActorRef = system.actorOf(Props[InstanceActor], "instances")
        val directory : ActorRef = system.actorOf(Props[ExecutorDirectoryServiceActor], "ExecutorDirectory")
        var dBActor : ActorRef = null
        if (dbConf != null)
          dBActor = system.actorOf(Props(new DBActor(dbConf)), name = "db")
        else
          dBActor = system.actorOf(Props(new DBActor()), name = "db")
        val apiActor : ActorRef = system.actorOf(Props(classOf[ClusteringApi], localIp), "api")
        val globalStatus : ActorRef = system.actorOf(Props[GlobalStatusActor], "globalStatus")
        val nodeMasterActor : ActorRef = system.actorOf(Props[NodeMasterActor], "nodeMasterActor")

        val future = dBActor ? ConnectionTest
        val result = Await.result(future, 3 seconds).asInstanceOf[ConnectionStatus]
        result match {
          case ConnectionStatus(true) =>
            log.info("DB connection succeed.")
          case ConnectionStatus(false) =>
            log.error("DB connection failed.")
            System.exit(1)
        }

        // Load codebase
        if(cli_config.input != null) {
          val loader: TestingCodebaseLoader = new TestingCodebaseLoader(cli_config.input)
          val testMethods = loader.getClassClusterMethods
          var version = loader.getVmConfig
          val datestring = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())
          // Add Tasks
            for (a <- testMethods.asScala.toList) {
              log.info(s"adding task ${a.classname}.${a.methodname} to table $datestring")
              var singleInstance: Boolean = true
              if (a.annotation.clusterType() == ClusterType.GROUPING)
                singleInstance = false

              // check if the executor should be run locally
              var run_locally = false
              if(a.annotation.expectedTraffic() == TrafficLoad.MAJOR)
                run_locally = true

              // Add Task to dependency tree
              instanceActor ! AddTask(datestring, a.annotation.members().toList, Task(loader.getRawTestClass(a.classname), a.classname, a.methodname, singleInstance, run_locally), version)

              // Add Task to Database
              dBActor ! CreateTask(s"${a.classname}.${a.methodname}", datestring)
            }
        }

        if(cli_config.debug)
          println(new PrivateMethodExposer(system)('printTree)())

        nodeMasterActor ! IncludeNode(Cluster(system).selfAddress)

        println("Press any key to stop...")
        StdIn.readLine()
        println("Shutting down the Cluster...")
        if(cli_config.debug)
          println(new PrivateMethodExposer(system)('printTree)())
        Await.result(system.terminate(), Duration.Inf)
        println("IMPORTANT! Please check your hypervisor if there is still a VM running.")
        println("Goodbye, have a nice day!")
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
        println("IMPORTANT! Please check your hypervisor if there is still a VM running.")
        println("Goodbye, have a nice day!")
        System.exit(0)
      }


    }
    case None =>
      System.exit(1)
  }
}
