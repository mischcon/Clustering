package de.oth.clustering.scala.vm

import java.net.URL

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Terminated}
import de.oth.clustering.java.communication._
import de.oth.clustering.java.vm.vagrant.configuration.{VagrantEnvironmentConfig, VagrantPortForwardingConfig}
import de.oth.clustering.scala.utils.telnet.TelnetClientPipelineFactory
import de.oth.clustering.scala.vm.messages._
import de.oth.clustering.scala.worker.messages._
import de.oth.clustering.scala.worker.traits.VMTaskWorkerTrait
import org.apache.http.impl.client.HttpClientBuilder

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
  * Created by mischcon on 3/20/17.
  */
class VMProxyActor extends Actor with ActorLogging with VMTaskWorkerTrait{

  private var uuid: String = _
  private var nodeActor: ActorRef = _
  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _
  private var vmActor: ActorRef = _
  private var instanceActor : ActorRef = _
  private var portMapping: Map[String, (String, Int)] = Map()
  private var scheduleGetTask: Cancellable = _
  private var haveSpaceForTasks: Boolean = _
  private var ready: Boolean = _

  self ! Init

  override def receive: Receive = {
    case Init                                          => log.debug("got Init");                                  handlerInit
    case SetVmActor(actor)                             => log.debug(s"got SetVmActor($actor)");                   handlerSetVmActor(actor)
    case SetInstanceActor(actor)                       => log.debug(s"got SetInstanceActor($actor)");             handlerSetInstanceActor(actor)
    case NotReadyJet(message)                          => log.debug(s"got NotReadyJet($message)");                handlerNotReadyJet(message)
    case SetVagrantEnvironmentConfig(config)           => log.debug(s"got SetVagrantEnvironmentConfig($config)"); handlerSetVagrantEnvironmentConfig(config)
    case SendTask(task)                      if ready  => /*log.debug(s"got SendTask($task)");*/                  handlerSendTask(task)
    case NoMoreTasks                         if ready  => log.debug("got NoMoreTasks");                           handlerNoMoreTasks
    case Executor(actor)                     if ready  => log.debug(s"got Executor($actor)");                     handlerExecutor(actor)
    case CannotGetExecutor                   if ready  => log.debug("got CannotGetExecutor");                     handlerCannotGetExecutor
    case Terminated(actor)                   if ready  => log.debug(s"got Terminated($actor)");                   handlerTerminated(actor)
    case request: RestApiRequest             if ready  => log.debug(s"got RestApiRequest($request)");             handlerRestApiRequest(request)
    case StillAlive                          if ready  => log.debug("got StillAlive");                            handlerStillAlive
    case x: Any                              if !ready => log.debug(s"got Message $x but NotReadyJet");           handlerNotReady(x)
  }

  private def handlerInit = {
    ready = false
    uuid = self.path.name.split("_"){1}
    nodeActor = context.parent
    haveSpaceForTasks = true
    nodeActor ! GetVmActor(self)
    nodeActor ! GetInstanceActor
  }

  private def handlerSetVmActor(actor: ActorRef) = {
    this.vmActor = actor
    checkReady
  }

  private def handlerSetInstanceActor(actor: ActorRef) = {
    this.instanceActor = actor
    checkReady
  }

  private def handlerSetVagrantEnvironmentConfig(config: VagrantEnvironmentConfig) = {
    this.vagrantEnvironmentConfig = config
    getPortMapping
    checkReady
    if (scheduleGetTask == null)
      scheduleGetTask(5 seconds, 10 seconds)
  }

  private def handlerNotReady(any: Any) = {
    sender() ! NotReadyJet(any)
  }

  private def handlerNotReadyJet(any: Any) = {
    scheduleOnceRetry(5 seconds, sender(), any)
  }

  override def handlerSendTask(task: Task) = {
    if (scheduleGetTask != null)
      scheduleGetTask.cancel()
    scheduleGetTask = null
    if (haveSpaceForTasks) {
      haveSpaceForTasks = false
      sender() ! AcquireExecutor(self)
    } else
      sender() ! Failure(new Exception("no more tasks!"))
  }

  override def handlerNoMoreTasks = {
    if (scheduleGetTask != null)
      scheduleGetTask.cancel()
    scheduleGetTask = null
    vmActor ! NoMoreTasks
    nodeActor ! RemoveVmActor(self)
    context.stop(self)
  }

  override def handlerExecutor(executor: ActorRef) = {
    context.watch(executor)
  }

  override def handlerCannotGetExecutor = {
    handlerFailure
  }

  override def handlerTerminated(actorRef: ActorRef) = {
    log.debug(s"received TERMINATED from ${actorRef.path.toString}, which means that the task is done - now I have space for a new task!")
    handlerFailure
  }

  private def handlerFailure = {
    log.debug("releasing task - now I have space for a new task!")
    haveSpaceForTasks = true
    if (scheduleGetTask == null)
      scheduleGetTask(0 seconds, 10 seconds)
  }

  private def handlerSuccess(): Unit = {
    log.debug("task successfull - ask for new task")
    haveSpaceForTasks = true
    if (scheduleGetTask == null)
      scheduleGetTask(0 seconds, 10 seconds)
  }

  private def handlerRestApiRequest(request: RestApiRequest) = {
    val url = new URL(request.getUrl)
    if (portMapping.contains(url.getProtocol)) {
      val protocol = url.getProtocol
      val port = portMapping {
        url.getProtocol
      }._2
      val host = portMapping {
        url.getProtocol
      }._1
      val builder = new StringBuilder()
      builder.append(s"$protocol://$host:$port")
      if (url.getPath != null) builder.append(url.getPath)
      if (url.getQuery != null) builder.append(s"?${url.getQuery}")
      if (url.getRef != null) builder.append(s"#${url.getRef}")
      request.setUrl(builder.toString())
    }
    try {
      request.getMethod match {
        case "GET" =>
          val httpGet: GetRequest = new GetRequest(request)
          sendRequest(httpGet)
        case "POST" =>
          val httpPost: PostRequest = new PostRequest(request)
          sendRequest(httpPost)
        case "PUT" =>
          val httpPut: PutRequest = new PutRequest(request)
          sendRequest(httpPut)
        case "DELETE" =>
          val httpDelete: DeleteRequest = new DeleteRequest(request)
          sendRequest(httpDelete)
      }
    }
  }

  override def handlerStillAlive = {
    //log.debug(s"forward StillAlive from ${msg.self} to $vmActor")
    //vmActor.forward(StillAlive)
    checkSsh
  }

  private def checkReady = {
    if (vmActor != null && instanceActor != null && vagrantEnvironmentConfig != null)
      ready = true
  }

  private def getPortMapping = {
    portMapping = Map()
    for (vmConfig <- vagrantEnvironmentConfig.vmConfigs().asScala) {
      for (vagrantNetworkConfig <- vmConfig.vagrantNetworkConfigs().asScala) {
        vagrantNetworkConfig match {
          case x: VagrantPortForwardingConfig => {
            if (x.guestPort() == 22)
              portMapping += s"${x.service()}_${vmConfig.name()}" -> (x.hostIp(), x.hostPort())
            else
              portMapping += x.service() -> (x.hostIp(), x.hostPort())
          }
        }
      }
    }
  }

  private def checkSsh = {
    import java.net.InetSocketAddress
    import java.util.concurrent.Executors

    import org.jboss.netty.bootstrap.ClientBootstrap
    import org.jboss.netty.channel.ChannelFuture
    import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory

    var result = true

    portMapping.filter(_._1.contains("ssh")).map(_._2).toList.foreach( entry => {
      val host = entry._1
      val port = entry._2
      log.debug(s"check ssh for host $host on port $port")

      // Configure the client.
      val bootstrap = new ClientBootstrap(
        new NioClientSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))

      // Set up the pipeline factory.
      bootstrap.setPipelineFactory(new TelnetClientPipelineFactory)
      try {
        // Start the connection attempt.
        val future: ChannelFuture = bootstrap.connect(new InetSocketAddress(host, port))

        // Wait until the connection attempt succeeds or fails.
        val channel = future.awaitUninterruptibly.getChannel
        if (!future.isSuccess) {
          result = false
          bootstrap.releaseExternalResources()
        }
        // Close the connection.  Make sure the close operation ends because
        // all I/O operations are asynchronous in Netty.
        channel.close().awaitUninterruptibly

        // Shut down all thread pools to exit.
        bootstrap.releaseExternalResources()
      } catch {
        case e: Exception => log.debug(s"could not connect to $host:$port")
      }
    })
    log.debug(s"send $result to ${sender()}")
    sender() ! result
  }

  private def sendRequest(httpRequest: HttpRequest) = {
    log.debug("creating HttpClient")
    val client = HttpClientBuilder.create.build
    log.debug("getting response")
    val req = httpRequest.getRequest
    log.debug(s"request: ${httpRequest.getUrl}")
    try {
      val response = client.execute(req)
      log.debug("parsing")
      val output = new RestApiResponse(response)
      log.debug("sending")
      sender() ! output
      response.close()
    } catch {
      case exception: Exception =>
        sender() ! exception
    }
    client.close()
    handlerSuccess()
  }

  private def scheduleGetTask(delay: FiniteDuration, interval: FiniteDuration): Unit = {
    scheduleGetTask = context.system.scheduler.schedule(delay, interval, instanceActor, GetTask(vagrantEnvironmentConfig.version()))(context.dispatcher, self)
  }

  private def scheduleOnceRetry(delay: FiniteDuration, receive: ActorRef, message: Any) = {
    context.system.scheduler.scheduleOnce(delay, receive, message)(context.dispatcher, self)
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }
}
