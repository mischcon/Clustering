package vm

import java.net.{URI, URL}

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Terminated}
import akka.util.Timeout
import communication._
import org.apache.http.impl.client.HttpClientBuilder
import vm.messages._
import vm.vagrant.configuration.{VagrantEnvironmentConfig, VagrantPortForwardingConfig}
import worker.messages._
import akka.pattern._
import vm.vagrant.util.Service

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.control

/**
  * Created by mischcon on 3/20/17.
  */
class VMProxyActor extends Actor with ActorLogging {

  private var uuid: String = _
  private var nodeActor: ActorRef = _
  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _
  private var vmActor: ActorRef = _
  private var instanceActor : ActorRef = _
  private var portMapping: Map[String, (String, Int)] = Map()
  private var cancellableGetVagrantEnvironmentConfig: Cancellable = _
  private var cancellableGetTask: Cancellable = _
  private var haveSpaceForTasks: Boolean = _
  import context.dispatcher

  self ! Init

  override def receive: Receive = {
    case Init => init
    case SetVagrantEnvironmentConfig(vagrantEnvironmentConfig) => {
      log.debug("got SetVagrantEnvironmentConfig")
      this.vagrantEnvironmentConfig = vagrantEnvironmentConfig
      log.debug("deregisterGetVagrantEnvironmentConfig")
      deregisterGetVagrantEnvironmentConfig
      log.debug("getPortMapping")
      getPortMapping
      registerGetTask
    }
    case SetVmActor(vmActor) => this.vmActor = vmActor; initGetVagrantEnvironmentConfig
    case SetInstanceActor(instanceActor) => this.instanceActor = instanceActor
    case NotReadyJet => registerGetVagrantEnvironmentConfig
    case SendTask(task) if haveSpaceForTasks => {
      haveSpaceForTasks = false
      deregisterGetTask
      sender() ! AcquireExecutor(vagrantEnvironmentConfig.version(), self)
    }
    case SendTask(task) if !haveSpaceForTasks => sender() ! Failure(new Exception("no more tasks!"))
    case NoMoreTasks => destroyVm
    case Executor(executor) => context.watch(executor)
    case CannotGetExecutor => handleFailure()
    case t : Terminated => {
      log.debug(s"received TERMINATED from ${t.actor.path.toString}, which means that the task is done - now I have space for a new task!")
      handleFailure()
    }
    case request: RestApiRequest =>
      val url = new URL(request.getUrl)
      if (portMapping.contains(url.getProtocol)) {
        val protocol = url.getProtocol
        val port = portMapping{url.getProtocol}._2
        val host = portMapping{url.getProtocol}._1
        val builder = new StringBuilder()
        builder.append(s"$protocol://$host:$port")
        if (url.getPath != null) builder.append(url.getPath)
        if (url.getQuery != null) builder.append(s"?${url.getQuery}")
        if (url.getRef != null) builder.append(s"#${url.getRef}")
        request.setUrl(builder.toString())
      }
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

  private def init = {
    uuid = self.path.name.split("_"){1}
    nodeActor = context.parent
    haveSpaceForTasks = true
    nodeActor ! GetVmActor
    nodeActor ! GetInstanceActor
  }

  private def initGetVagrantEnvironmentConfig = {
    if (vmActor != null) {
      vmActor ! GetVagrantEnvironmentConfig
    }
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


  private def registerGetVagrantEnvironmentConfig = {
    if (cancellableGetVagrantEnvironmentConfig == null)
      cancellableGetVagrantEnvironmentConfig = context.system.scheduler.schedule(10 seconds, 60 seconds, vmActor, GetVagrantEnvironmentConfig)
  }

  private def deregisterGetVagrantEnvironmentConfig = {
    if (cancellableGetVagrantEnvironmentConfig != null) {
      cancellableGetVagrantEnvironmentConfig.cancel()
      cancellableGetVagrantEnvironmentConfig == null
    }
  }

  private def registerGetTask = {
    if (cancellableGetTask == null)
      cancellableGetTask = context.system.scheduler.schedule(0 seconds, 1 seconds, instanceActor, GetTask)
  }

  private def deregisterGetTask = {
    if (cancellableGetTask != null) {
      cancellableGetTask.cancel()
      cancellableGetTask == null
    }
  }

  private def sendRequest(httpRequest: HttpRequest) = {
    log.debug("creating HttpClient")
    val client = HttpClientBuilder.create.build
    log.debug("getting response")
    val req = httpRequest.getRequest
    log.debug(s"request: ${httpRequest.getUrl}")
    val response = client.execute(req)
    log.debug("parsing")
    val output = new RestApiResponse(response)
    log.debug("sending")
    sender() ! output
    response.close()
    client.close()
  }

  def handleFailure(): Unit ={
    log.debug("releasing task - now I have space for a new task!")
    haveSpaceForTasks = true
    registerGetTask
  }

  private def destroyVm = {
    vmActor ! TasksDone
    nodeActor ! RemoveVmActor(self)
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }
}
