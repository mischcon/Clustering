package vm

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

/**
  * Created by mischcon on 3/20/17.
  */
class VMProxyActor extends Actor with ActorLogging {

  private var uuid: String = _
  private var nodeActor: ActorRef = _
  private var vagrantEnvironmentConfig: VagrantEnvironmentConfig = _
  private var vmActor: ActorRef = _
  private var instanceActor : ActorRef = _
  private var portMapping: Map[Service, Int] = Map()
  private var cancellable: Cancellable = _
  private var cancellableGetTask: Cancellable = _
  private var haveSpaceForTasks: Boolean = _
  import context.dispatcher

  self ! Init

  override def receive: Receive = {
    case Init => init
    case SetVagrantEnvironmentConfig(vagrantEnvironmentConfig) => {
      this.vagrantEnvironmentConfig = vagrantEnvironmentConfig
      getPortMapping
      if (cancellable != null) {
        cancellable.cancel()
        cancellable = null
      }
      registerGetTask
    }
    case NotReadyJet => registerScheduler
    case GetTask if haveSpaceForTasks => instanceActor ! GetTask(vagrantEnvironmentConfig)
    case GetTask if !haveSpaceForTasks => cancellableGetTask.cancel(); cancellableGetTask = null
    case SendTask(task) if haveSpaceForTasks => {
      haveSpaceForTasks = false
      sender() ! AcquireExecutor(vagrantEnvironmentConfig.version(), self)
    }
    case SendTask(task) if !haveSpaceForTasks => sender() ! Failure(new Exception("no more tasks!"))
    case Executor(executor) => context.watch(executor)
    case CannotGetExecutor => handleFailure()
    case t : Terminated => {
      log.debug(s"received TERMINATED from ${t.actor.path.toString}, which means that the task is done - now I have space for a new task!")
      handleFailure()
    }
    case request : RestApiRequest =>
      // ToDo: URL anpassen
      request.getMethod match {
        case "GET" =>
          val httpGet : GetRequest = new GetRequest(request)
          sendRequest(httpGet)
        case "POST" =>
          val httpPost : PostRequest = new PostRequest(request)
          sendRequest(httpPost)
        case "PUT" =>
          val httpPut : PutRequest = new PutRequest(request)
          sendRequest(httpPut)
        case "DELETE" =>
          val httpDelete : DeleteRequest = new DeleteRequest(request)
          sendRequest(httpDelete)
      }
  }

  private def init = {
    uuid = self.path.name.split("_"){1}
    nodeActor = context.parent
    haveSpaceForTasks = true
    implicit val timeout = Timeout(15 seconds)
    val vmActorFuture = nodeActor ? GetVmActor
    Await.result(vmActorFuture, timeout.duration) match {
      case SetVmActor(vmActor) => this.vmActor = vmActor
      case _ => log.error("did not receive vmActor")
    }
    log.debug(s"VMActor: ${this.vmActor}")
    val instanceFuture = nodeActor ? GetInstanceActor
    Await.result(instanceFuture, timeout.duration) match {
      case SetInstanceActor(instanceActor) => this.instanceActor = instanceActor
      case _ => ???
    }
    val vagrantEnvironmentConfigFuture = vmActor ? GetVagrantEnvironmentConfig
    Await.result(vagrantEnvironmentConfigFuture, timeout.duration) match {
      case SetVagrantEnvironmentConfig(vagrantEnvironmentConfig) => self ! SetVagrantEnvironmentConfig(vagrantEnvironmentConfig); registerGetTask
      case NotReadyJet => self ! NotReadyJet
    }
  }

  private def getPortMapping = {
    portMapping = Map()
    //TODO: vagrantEnvironmentConfig.vmConfigs().asScala.map(_.vagrantNetworkConfigs()).foreach{case x: VagrantPortForwardingConfig => portMapping += x.service() -> x.hostPort()}
  }


  private def registerScheduler = {
    if (cancellable == null)
      cancellable = context.system.scheduler.schedule(10 seconds, 60 seconds, vmActor, GetVagrantEnvironmentConfig)
  }

  private def registerGetTask = {
    if (cancellableGetTask == null)
      cancellableGetTask = context.system.scheduler.schedule(1 seconds, 1 seconds, self, GetTask)
  }

  def sendRequest(httpRequest: HttpRequest) = {
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

    Thread.sleep(1000)
    registerGetTask
  }

  override def preStart(): Unit = {
    log.debug(s"hello from ${self.path.name}")
  }

  override def postStop(): Unit = {
    log.debug(s"goodbye from ${self.path.name}")
  }
}
