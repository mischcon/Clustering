import utils.{ClusterOptionParser, Config}

object ClusterMain extends App{

  val parser : ClusterOptionParser = new ClusterOptionParser()
  parser.parser.parse(args, Config()) match {
    case Some(config) =>
      val loader : TestingCodebaseLoader = new TestingCodebaseLoader(config.input)
    case None =>
      System.exit(1)
  }
}
