import sbtassembly.Plugin.AssemblyKeys._

assemblySettings

jarName in assembly := "Clustering.jar"
mainClass in assembly := Some("de.oth.clustering.scala.ClusterMain")
