package webui

import java.io._
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import scala.collection.JavaConverters._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import de.oth.clustering.java._
import spray.json.{DefaultJsonProtocol, JsObject}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import scala.concurrent.duration._
import scala.io.StdIn
import DefaultJsonProtocol._
import akka.http.scaladsl.Http
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source, StreamConverters}
import clustering.ClusterType
import sun.misc.IOUtils
import utils.db.CreateTask
import worker.messages.{AddTask, Task}

import scala.util.{Failure, Success}

/**
  * Created by mischcon on 10.04.17.
  */

case class UploadJar(content : Array[Byte])

class ClusteringApi extends Actor with ActorLogging with Directives with SprayJsonSupport{

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher
  implicit val timeout = Timeout(2 seconds)
  implicit val system = context.system

  implicit val uploadJarForamt = jsonFormat1(UploadJar)

  val instanceActor = context.system.actorSelection("/user/instances")
  val dBActor = context.system.actorSelection("/user/db")

  //  object MyJsonProtocol extends DefaultJsonProtocol {
//    implicit val format = jsonFormat1(UploadJar)
//  }

  val routes: Route =
    path("api"/"upload") {
//      post {
//        entity(as[UploadJar]) {
//          obj => handleJarUpload(obj); complete("Ok")
//        }
      post {
        withoutSizeLimit {
          extractDataBytes {
            bytes =>
              val file = File.createTempFile("DEBUG","b")
              val sink = FileIO.toPath(file.toPath)
              val writing = bytes.runWith(sink)
              onSuccess(writing) { result =>
                result.status match {
                  case Success(_) => {
                    val content = java.nio.file.Files.readAllBytes(file.toPath)
                    handleJarUpload(content)
                    file.delete()
                    complete("upload done")
                  }
                  case Failure(e) => file.delete(); complete(500, e.getMessage)
                }
              }
          }
        }
      }
    }~
    path("api"/"hi") {
      get {
        complete("hallo du da")
      }
    }

  override def receive: Receive = {
    case a => println(s"received $a")
  }

  def handleJarUpload(content : Array[Byte]) : Unit = {
    val loader : TestingCodebaseLoader = new TestingCodebaseLoader(content)
    val testMethods = loader.getClassClusterMethods
    val datestring = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())

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
  }

  // Start the Server and configure it with the route config
  val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 8080)
  log.info("JAR file upload now possible via 0.0.0.0:8080/api/upload")
}

object Main extends App {
  val config = ConfigFactory.load()
  val system : ActorSystem = ActorSystem("the-cluster", config.getConfig("master").withFallback(config))

  val api = system.actorOf(Props[ClusteringApi], "api")

  StdIn.readLine()
  system.terminate()
}
