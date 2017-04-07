name := "Clustering"
version := "1.0"
scalaVersion := "2.12.1"

// documentation settings
scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")
javacOptions  in (Compile,doc) ++= Seq("-notimestamp", "-linksource")
autoAPIMappings := true

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

val akkaVersion = "2.4.17"
val akkaHttpVersion = "10.0.5"
val jrubyVersion = "9.1.8.+"
val sbtIoVersion = "1.0.0-+"

libraryDependencies ++= Seq(

  // database
  "mysql" % "mysql-connector-java" % "5.1.24",

  // httpclient
  "org.apache.httpcomponents" % "httpclient" % "4.5.3",

  // json
  "com.google.code.gson" % "gson" % "1.7.1",

  // config parser
  "com.github.scopt"  %% "scopt" % "3.5.0",

  // logger
  "ch.qos.logback" % "logback-classic" % "1.2.2",

  // play
  "com.typesafe.play" %% "play" % "2.6.0-+",

  // akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-camel" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

  // akka-http
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,

  //JRuby
  "org.jruby" % "jruby-complete" % jrubyVersion,
 
  //Sbt-IO
  "org.scala-sbt" %% "io" % sbtIoVersion
)