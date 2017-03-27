name := "Clustering"
version := "1.0"
scalaVersion := "2.12.1"

// *****************************************************************
// documentation settings
scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")
javacOptions in (Compile,doc) ++= Seq("-notimestamp", "-linksource")
autoAPIMappings := true
// *****************************************************************

val akkaVersion = "2.4.17"
val akkaHttpVersion = "10.0.5"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.24",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-camel" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,

  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion
)