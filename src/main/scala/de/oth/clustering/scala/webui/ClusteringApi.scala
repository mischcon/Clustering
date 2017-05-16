package de.oth.clustering.scala.webui

import java.io._
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.{ByteString, Timeout}
import de.oth.clustering.java.clustering.{ClusterType, TrafficLoad}
import de.oth.clustering.java._
import de.oth.clustering.java.utils.TestingCodebaseLoader
import spray.json.DefaultJsonProtocol._
import de.oth.clustering.scala.utils.PrivateMethodExposer
import de.oth.clustering.scala.utils.db._
import de.oth.clustering.scala.utils.messages.{GetGlobalSystemAttributes, GetVMInfos, GlobalSystemAttributes, VMInfos}
import de.oth.clustering.scala.webui.messages.WebUIMessage
import de.oth.clustering.scala.worker.messages.{AddTask, Task}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}


case class UploadJar(content : Array[Byte]) extends WebUIMessage

/**
  * = Cluster API =
  * Available @ <code>http://[ip]:8080/api</code>
  * <br>
  * <br>
  * Endpoints:
  * {{{
  *   GET /api - landing page
  *
  *       GET  /api/upload                   drag & drop .jar file upload
  *       POST /api/upload                   .jar file upload
  *       GET  /api/reporting                reporting API; check available task sets
  *       GET  /api/reporting/[table name]   single report for requested task set; check status of done tasks
  *       GET  /api/status                   health status (CPU load, RAM usage etc.) of all attached phys. nodes
  *       GET  /api/vms                      information about amount of deployed VMs on each node
  *       GET  /api/tree                     actor tree w/i the cluster
  * }}}
  * @param ip ip to bind API on
  */
class ClusteringApi(ip : String) extends Actor with ActorLogging with Directives with SprayJsonSupport {
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher
  implicit val timeout = Timeout(5 seconds)
  implicit val system = context.system

  implicit val uploadJarForamt = jsonFormat1(UploadJar)

  val instanceActor = context.actorSelection("/user/instances")
  val dBActor = context.actorSelection("/user/db")
  val globalStatusActor = context.actorSelection("/user/globalStatus")

  val port = 8080

  val contentTypeHtml = ContentTypes.`text/html(UTF-8)`
  val contentTypeText = ContentTypes.`text/plain(UTF-8)`

  var dataPath = new String

  def getApiContent: String = {
    s"""
<!DOCTYPE html>
<html>
  <title>Cluster API</title>
  <body>
    <h1>welcome to cluster API</h1>
    <br>
    <ul><h3>available endpoints</h3>
      <li><a href='http://$ip:$port/api/upload' style='font-size: 20px;'>/upload</a></li>
      <li><a href='http://$ip:$port/api/reporting' style='font-size: 20px;'>/reporting</a></li>
      <li><a href='http://$ip:$port/api/status', style='font-size: 20px;'>/status</a></li>
      <li><a href='http://$ip:$port/api/vms', style='font-size: 20px;'>/vms</a></li>
      <li><a href='http://$ip:$port/api/tree' style='font-size: 20px;'>/tree</a></li>
    </ul>
  </body>
</html>"""
  }

  def getApiReportingContent: String = {
    s"""
<!DOCTYPE html>
<html>
  <title>Reporting</title>
  <body>
    <h1>available sets</h1>
    $report
  </body>
</html>"""
  }

  def getApiSingleReportContent(name : String): String = {
    s"""
<!DOCTYPE html>
<html>
  <title>Reporting $name</title>
  <head>
    <script language='javascript' type='text/javascript' src='https://code.jquery.com/jquery-1.12.4.min.js'></script>
    <script language='javascript' type='text/javascript' src='https://cdn.datatables.net/1.10.15/js/jquery.dataTables.min.js'></script>
    <link rel='stylesheet' href='https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css'>
  </head>
  <style>
    td.details-control {
        background: url('http://$ip:$port/images/details_open.png') no-repeat center center;
        cursor: pointer;
    }
    tr.shown td.details-control {
        background: url('http://$ip:$port/images/details_close.png') no-repeat center center;
    }
  </style>
  <script>
    function format ( d ) {
        // `d` is the original data object for the row
        return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">'+
            '<tr>'+
                '<td align="right">'+d.result+'</td>'+
            '</tr>'+
        '</table>';
    }

     $$(document).ready(function() {
        var table =  $$('#report').DataTable( {
            "ajax": "http://$ip:$port/files/data.json",
            "columns": [
                {
                    "className":      'details-control',
                    "orderable":      false,
                    "data":           null,
                    "defaultContent": ''
                },
                { "data": "end_state" },
                { "data": "method" },
                { "data": "started_at" },
                { "data": "finished_at" },
                { "data": "time_spent" }
            ],
            "order": [[1, 'asc']]
        } );

        // Add event listener for opening and closing details
         $$('#report tbody').on('click', 'td.details-control', function () {
            var tr =  $$(this).closest('tr');
            var row = table.row( tr );

            if ( row.child.isShown() ) {
                // This row is already open - close it
                row.child.hide();
                tr.removeClass('shown');
            }
            else {
                // Open this row
                row.child( format(row.data()) ).show();
                tr.addClass('shown');
            }
        } );
    } );
  </script>
  <body>
    <table id='report' class='display' cellspacing='0' width='100%'>
      <thead>
        <tr align="center">
          <th></th>
          <th>end state</th>
          <th>method</th>
          <th>started @</th>
          <th>finished @</th>
          <th>time spent</th>
        </tr>
      </thead>
      <tfoot>
        <tr align="center">
          <th></th>
          <th>end state</th>
          <th>method</th>
          <th>started @</th>
          <th>finished @</th>
          <th>time spent</th>
        </tr>
      </tfoot>
    </table>
  </body>
</html>"""
  }

  def getDropzoneContent: String = {
    s"""
<!DOCTYPE html>
<html>
  <title>Upload your .jar file</title>
  <head>
    <script src='https://rawgit.com/enyo/dropzone/master/dist/dropzone.js'></script>
    <link rel='stylesheet' href='https://rawgit.com/enyo/dropzone/master/dist/dropzone.css'>
  </head>
  <script>
    Dropzone.options.jarDropzone = {
      maxFilesize: 200,
      addRemoveLinks: true,
      headers: {'Content-Type': 'application/octet-stream', 'Accept-Encoding': 'gzip,deflate'},
      acceptedFiles: '.jar',
      sending: function(file, xhr) {
           var _send = xhr.send;
           xhr.send = function() {
             _send.call(xhr, file);
           };
         }
    };
  </script>
  <body>
    <h1>Upload your .jar file</h1>
    <br>
    <form
      id='jar-dropzone'
      class='dropzone'
      method='POST'
      action='http://$ip:$port/api/upload'
      enctype='multipart/form-data'>
      <div class="fallback">
        <input name="file" type="file"/>
      </div>
    </form>
  </body>
</html>"""
  }

  def report: String = {
    val future = dBActor ? GetTables
    val result = Await.result(future, timeout.duration).asInstanceOf[Tables]
    val sb = new StringBuilder
    sb.append("<ul>")
    for (name <- result.names) {
      sb.append("<li><a href='http://")
      sb.append(ip)
      sb.append(":")
      sb.append(port)
      sb.append("/api/reporting/")
      sb.append(name)
      sb.append("' style='font-size: 16px;'>")
      sb.append(name)
      sb.append("</a></li>")
    }
    sb.append("</ul>")
    sb.toString()
  }

  def report(tableName : String): Unit = {
    val future = dBActor ? GenerateJsonReport(tableName)
    val result = Await.result(future, timeout.duration).asInstanceOf[Report]
    result match {
      case Report(path) =>
        dataPath = path
        log.debug("API is able to access report data.")
      case _ =>
        log.debug("Something went wrong. API cannot access report data.")
    }
  }

  def printTree: String = {
    new PrivateMethodExposer(system)('printTree)().toString
  }

  def printStatus: String = {
    val future = globalStatusActor ? GetGlobalSystemAttributes
    val result = Await.result(future, timeout.duration).asInstanceOf[GlobalSystemAttributes]
    result match {
      case GlobalSystemAttributes(globalAttributes) => {
        var sb = new StringBuilder
        sb.append("GLOBAL STATUS:\n")
        for(address <- globalAttributes){
          var src = address.toString().split("_"){1}.split("/"){0}
          sb.append(s"   $src\n")
          for(attributes <- address._2){
            sb.append(s"      ${attributes._1} -> ${attributes._2}\n")
          }
          sb.append("   ----------\n")
        }
        sb.toString()
      }
      case _ =>
        "Something went wrong. API cannot access global status."
    }
  }

  def printVMs: String = {
    val future = globalStatusActor ? GetVMInfos
    val result = Await.result(future, timeout.duration).asInstanceOf[VMInfos]
    result match {
      case VMInfos(infos) => {
        var sb = new StringBuilder
        sb.append("VMs per Node:\n")
        for(x <- infos){
          sb.append(s"   ${x._1} ---> ${x._2}\n")
        }
        sb.toString()
      }
      case _ => "Something went wrong. API cannot access VM infos."
    }
  }

  val routes : Route =
    path("files" / "data.json") {
      get {
        getFromFile(dataPath)
      }
    } ~
    path("images" / "details_open.png") {
      get {
        getFromFile("src/main/resources/de.oth.de.oth.clustering.java.clustering.scala.webui/images/details_open.png")
      }
    } ~
    path("images" / "details_close.png") {
      get {
        getFromFile("src/main/resources/de.oth.de.oth.clustering.java.clustering.scala.webui/images/details_close.png")
      }
    } ~
    path("api") {
      get {
        complete(HttpEntity(contentTypeHtml, getApiContent))
      }
    } ~
    path("api" / "upload") {
      get {
        complete(HttpEntity(contentTypeHtml, getDropzoneContent))
      } ~
      post {
        withoutSizeLimit {
          extractDataBytes {
            bytes => handleUpload(bytes)
          }
        }
      }
    } ~
    path("api" / "reporting") {
      get {
        complete(HttpEntity(contentTypeHtml, getApiReportingContent))
      }
    } ~
    path("api" / "reporting" / Segment) {
      (name) =>
        get {
          report(name)
          complete(HttpEntity(contentTypeHtml, getApiSingleReportContent(name)))
        }
    } ~
    path("api" / "status") {
      get {
        complete(HttpEntity(contentTypeText, printStatus))
      }
    } ~
    path("api" / "vms") {
      get {
        complete(HttpEntity(contentTypeText, printVMs))
      }
    } ~
    path("api" / "tree") {
      get {
        complete(HttpEntity(contentTypeText, printTree))
      }
    }

  override def receive: Receive = {
    case a => println(s"received $a")
  }

  def handleUpload(bytes : Source[ByteString, Any]): Route = {
    val file = File.createTempFile(java.util.UUID.randomUUID.toString, ".bin")
    file.deleteOnExit()
    val sink = FileIO.toPath(file.toPath)
    val writing = bytes.runWith(sink)
    onSuccess(writing) { result =>
      result.status match {
        case Success(_) =>
          val content = Files.readAllBytes(file.toPath)
          val loader : TestingCodebaseLoader = new TestingCodebaseLoader(content)
          val testMethods = loader.getClassClusterMethods
          val datestring = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())
          val version = loader.getVmConfig
          for (a <- testMethods.asScala.toList) {
            println(s"adding task ${a.classname}.${a.methodname} to table $datestring")
            var singleInstance: Boolean = true
            if (a.annotation.clusterType() == ClusterType.GROUPING)
              singleInstance = false

            // check if the executor should be run locally
            var run_locally = false
            if(a.annotation.expectedTraffic() == TrafficLoad.MAJOR)
              run_locally = true

            // Add Task to dependency tree
            instanceActor ! AddTask(
              datestring, a.annotation.members().toList,
              Task(loader.getRawTestClass(a.classname), a.classname, a.methodname, singleInstance, run_locally), version)

            // Add Task to Database
            dBActor ! CreateTask(s"${a.classname}.${a.methodname}", datestring)
          }
          file.delete()
          complete(201 -> "Upload successful")
        case Failure(e) => file.delete(); complete(500, e.getMessage)
      }
    }
  }

  // Start the Server and configure it with the route config
  val bindingFuture = Http().bindAndHandle(routes, ip, port)
  log.info(s"API is now available visit http://$ip:$port/api")
  log.info(s"JAR file upload now possible via http://$ip:$port/api/upload")
}
