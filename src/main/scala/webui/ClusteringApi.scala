package webui

import java.io._
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.{ByteString, Timeout}
import clustering.ClusterType
import de.oth.clustering.java._
import spray.json.DefaultJsonProtocol._
import utils.db.CreateTask
import worker.messages.{AddTask, Task}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

/**
  * Created by mischcon on 10.04.17.
  */

case class UploadJar(content : Array[Byte])

class ClusteringApi(ip : String) extends Actor with ActorLogging with Directives with SprayJsonSupport{

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher
  implicit val timeout = Timeout(2 seconds)
  implicit val system = context.system

  implicit val uploadJarForamt = jsonFormat1(UploadJar)

  val instanceActor = context.actorSelection("/user/instances")
  val dBActor = context.actorSelection("/user/db")

  val routes: Route =
    path("api"/"upload") {
      post {
        withoutSizeLimit {
          extractDataBytes {
            bytes => handleUpload(bytes)
          }
        }
      }
    }

  override def receive: Receive = {
    case a => println(s"received $a")
  }

  def handleUpload(bytes : Source[ByteString, Any]) ={
    val file = File.createTempFile(new Random().nextString(15),"b")
    val sink = FileIO.toPath(file.toPath)
    val writing = bytes.runWith(sink)
    onSuccess(writing) { result =>
      result.status match {
        case Success(_) => {
          val content = java.nio.file.Files.readAllBytes(file.toPath)
          val loader : TestingCodebaseLoader = new TestingCodebaseLoader(content)
          val testMethods = loader.getClassClusterMethods
          val datestring = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())

          for (a <- testMethods.asScala.toList) {
            println(s"adding task ${a.classname}.${a.methodname} to table $datestring")
            var singleInstance: Boolean = true
            if (a.annotation.clusterType() == ClusterType.GROUPING)
              singleInstance = false

            // Add Task to dependency tree
            instanceActor ! AddTask(datestring, a.annotation.members().toList, Task(loader.getRawTestClass(a.classname), a.classname, a.methodname, singleInstance))

            // Add Task to Database
            dBActor ! CreateTask(s"${a.classname}.${a.methodname}", datestring)
          }
          file.delete()
          complete("upload done")
        }
        case Failure(e) => file.delete(); complete(500, e.getMessage)
      }
    }
  }

  // Start the Server and configure it with the route config
  val bindingFuture = Http().bindAndHandle(routes, ip, 8080)
  log.info(s"JAR file upload now possible via $ip:8080/api/upload")
}
