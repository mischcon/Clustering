import utils.{ClusterOptionParser, Config}

object ClusterMain extends App{

  val parser : ClusterOptionParser = new ClusterOptionParser()
  parser.parser.parse(args, Config()) match {
    case Some(config) =>
      /* TODO read out config and create ActorSystem accordingly */
    case None =>
      System.exit(1)
  }
}
