

name := "akka-hbase"

version := "0.1"

scalaVersion := "2.11.1"

val akkaVersion = "2.5.12"

val akkaHttpVersion = "10.0.13"

libraryDependencies ++= Seq(
  "eu.bitwalker" % "UserAgentUtils" % "1.14",

  "com.lightbend.akka" %% "akka-stream-alpakka-hbase" % "0.19",

  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.19",

  "com.typesafe" % "config" % "1.3.2",

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,

  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

  "org.scalatest" %% "scalatest" % "2.2.0" % "test",

  "org.scala-lang" % "scala-reflect" % "2.11.1",

  "log4j" % "log4j" % "1.2.17",

  "org.slf4j" % "slf4j-simple" % "1.7.25",

  "com.flipkart" % "hbase-object-mapper" % "1.3",

  // Add dependency on ScalaFX library
  "org.scalafx" %% "scalafx" % "8.0.144-R12",

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,

  "org.apache.phoenix" % "phoenix-core" % "4.6.0-HBase-1.1" excludeAll
    ExclusionRule(organization = "sqlline"),

  "org.apache.hbase" % "hbase-server" % "1.1.2"

)

// Add dependency on JavaFX library based on JAVA_HOME variable
unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

