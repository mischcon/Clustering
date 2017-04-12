package webui

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import de.oth.clustering.java._
import webui.messages.UploadJar

import scala.concurrent.duration._

/**
  * Created by mischcon on 10.04.17.
  */
class ClusteringApi extends Actor with ActorLogging{

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher
  implicit val timeout = Timeout(2 seconds)

  val routes: Route =
    path("api"/"upload") {
      post {
        entity(as[UploadJar]) {
          obj => handleJarUpload(obj); complete("Ok")
        }
      }
    }

  override def receive: Receive = ???

  def handleJarUpload(content : UploadJar) : Unit = {
    val loader : TestingCodebaseLoader = new TestingCodebaseLoader(content.content)
    val testMethods = loader.getClassClusterMethods
  }
}
