package vm

import java.net.URL

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Terminated}
import communication._
import org.apache.http.impl.client.HttpClientBuilder
import vm.messages._
import vm.vagrant.configuration.{VagrantEnvironmentConfig, VagrantPortForwardingConfig}
import worker.messages._
import worker.traits.VMTaskWorkerTrait

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.duration.DurationInt

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
  private var cancellableGetVagrantEnvironmentConfig: Cancellable = _
  private var cancellableGetTask: Cancellable = _
  private var haveSpaceForTasks: Boolean = _

  self ! Init

  override def receive: Receive = {
    case Init => log.debug("got Init"); init
    case SetVagrantEnvironmentConfig(vagrantEnvironmentConfig) => {
      log.debug("got SetVagrantEnvironmentConfig")
      this.vagrantEnvironmentConfig = vagrantEnvironmentConfig
      log.debug("deregisterGetVagrantEnvironmentConfig")
      deregisterGetVagrantEnvironmentConfig
      log.debug("getPortMapping")
      getPortMapping
      log.debug("registerGetTask")
      registerGetTask
    }
    case SetVmActor(vmActor) => log.debug("got SetVmActor");this.vmActor = vmActor; initGetVagrantEnvironmentConfig
    case SetInstanceActor(instanceActor) => log.debug("got SetInstanceActor");this.instanceActor = instanceActor
    case NotReadyJet => log.debug("got NotReadyJet"); registerGetVagrantEnvironmentConfig
    case SendTask(task) => handleSendTask(task)
    case NoMoreTasks => handleNoMoreTasks()
    case Executor(executor) => handleExecutor(executor)
    case CannotGetExecutor => handleCannotGetExecutor()
    case t : Terminated => handleTerminated(t)
    case request: RestApiRequest =>
      log.debug("got RestApiRequest");
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
      } catch {
        case exception: Exception =>
          sender() ! exception
      }
    case StillAlive => handleStillAlive()

  }

  private def checkVMStillAlive() = {
    /**
      * @Oliver
      *        bitte prüfe an dieser Stelle, ob die VM an sich (und für SSP: ob der tomcat) noch läuft
      *        und erreichbar ist. Ist für mich sehr wichtig, weil ich keine ahnung habe, ob eine "TestFailException"
      *        geworfen worden ist, weil der Test wirklich gefailed ist - oder weil die VM nicht erreichbar ist (z.B. IOException)
      *
      *        Wenn ja: @return true
      *        Wenn nein: @return false
      *
      *        Merci!
      */
    true
  }

  private def init = {
    uuid = self.path.name.split("_"){1}
    nodeActor = context.parent
    haveSpaceForTasks = true
    nodeActor ! GetVmActor(self)
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
    if (cancellableGetVagrantEnvironmentConfig == null) {
      log.debug(s"register GetVagrantEnvironmentConfig scheduler")
      cancellableGetVagrantEnvironmentConfig = context.system.scheduler.schedule(10 seconds, 60 seconds, vmActor, GetVagrantEnvironmentConfig)(context.dispatcher, self)
    }
  }

  private def deregisterGetVagrantEnvironmentConfig = {
    if (cancellableGetVagrantEnvironmentConfig != null) {
      cancellableGetVagrantEnvironmentConfig.cancel()
      cancellableGetVagrantEnvironmentConfig = null
      log.debug("cancellableGetVagrantEnvironmentConfig = null")
    }
  }

  private def registerGetTask = {
    if (cancellableGetTask == null) {
      log.debug(s"register GetTask scheduler with version: ${vagrantEnvironmentConfig.version()}")
      cancellableGetTask = context.system.scheduler.schedule(10 seconds, 10 seconds, instanceActor, GetTask(vagrantEnvironmentConfig.version()))(context.dispatcher, self)
    }
  }

  private def deregisterGetTask = {
    if (cancellableGetTask != null) {
      cancellableGetTask.cancel()
      cancellableGetTask = null
      log.debug("cancellableGetTask = null")
    }
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
    handleSuccess()
  }



  private def handleSuccess(): Unit = {
    log.debug("task successfull - ask for new task")
    haveSpaceForTasks = true
    instanceActor ! GetTask(vagrantEnvironmentConfig.version())
    registerGetTask
  }

  private def handleFailure(): Unit ={
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

  override def handleSendTask(task: Task): Unit = {
    if(haveSpaceForTasks){
      log.debug(s"got SendTask, haveSpaceForTasks = $haveSpaceForTasks")
      haveSpaceForTasks = false
      log.debug("deregisterGetTask")
      deregisterGetTask
      log.debug(s"send AcquireExecutor(${vagrantEnvironmentConfig.version()}, $self)")
      sender() ! AcquireExecutor(self)
    } else {
      sender() ! Failure(new Exception("no more tasks!"))
    }
  }

  override def handleNoMoreTasks(): Unit = {
    log.debug("got NoMoreTasks")
    destroyVm
    deregisterGetTask
  }

  override def handleExecutor(executor: ActorRef): Unit = {
    log.debug("got Executor")
    context.watch(executor)
  }

  override def handleCannotGetExecutor(): Unit = {
    log.debug("got CannotGetExecutor")
    handleFailure()
  }

  override def handleTerminated(terminated: Terminated): Unit = {
    log.debug(s"received TERMINATED from ${terminated.actor.path.toString}, which means that the task is done - now I have space for a new task!")
    handleFailure()
  }

  override def handleStillAlive(): Unit = {
    log.debug("received StillAlive")
    sender() ! checkVMStillAlive
  }
}
