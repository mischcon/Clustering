package vm

import de.oth.clustering.java.TestingCodebaseLoader


/**
  * Created by oliver.ziegert on 22.03.2017.
  */


object test extends App{
  val loader: TestingCodebaseLoader = new TestingCodebaseLoader("TestJar3.jar")
  val config = loader.getVmConfig
  println(config)
}
