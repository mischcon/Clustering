package de.oth.clustering.scala.utils

import java.io.File


class ClusterOptionParser() {

  private val VERSION : String = "0.1 alpha"

  val parser = new scopt.OptionParser[Config]("Cluster") {
    head("Cluster", VERSION)

    opt[Unit]("debug").optional().action((_, c) => c.copy(debug = true)).text("Start in debug mode")
    opt[Unit]("verbose").optional().action((_, c) => c.copy(verbose = true)).text("Verbose mode")
    opt[Int]('p', "port").optional().action((port, config) => config.copy(port = port)).text("Use Port [Default: 2550]")
    help("help").text("prints this usage text")

    cmd("master").action( (_, c) => c.copy(mode = "master") ).
      text("run as master").
      children(
        opt[File]("db").optional().action((s, c) => c.copy(db = s)).valueName("<db.conf>").text("db config file"),
        opt[String]('i', "input").action((s, c) => c.copy(input = s)).valueName("<task jar>").text("jar file that contains the tasks")
      )

    cmd("client").action( (_, c) => c.copy(mode = "client"))
      .text("run as cluster client").children(
      opt[String]('s', "seed-node").optional().action((x, c) => c.copy(seednode = x)).valueName("<seed-node-ip:seed-node-port>").text(s"manually choose seed node ip"),
      opt[Unit]("without-de.oth.de.oth.clustering.java.clustering.scala.vm").optional().action((_, c) => c.copy(withVm = false)).text("client should not provide VMs"),
      opt[Unit]("without-executor").optional().action((_, c) => c.copy(withExecutor = false)).text("client should not provide Executors"),

      checkConfig(c => if (!c.withVm && !c.withExecutor) failure("a client that provides neither a VM nor an Executor is useless") else success)
    )

    checkConfig(c => if(List("master", "client").contains(c.mode)) success else failure("mode has to be either master or client"))
  }
}


case class Config(mode: String = "", debug : Boolean = false, db : File = null, input : String = null,
                  verbose : Boolean = false, seednode : String = "localhost", withVm : Boolean = true,
                  withExecutor : Boolean = true, port : Int = 2550)


