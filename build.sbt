

name := "akka-hbase"

version := "0.1"

scalaVersion := "2.11.1"

val akkaVersion = "2.5.12"

libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-hbase" % "0.19",

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,

  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

  "org.scalatest" %% "scalatest" % "2.2.0" % "test",

  "org.scala-lang" % "scala-reflect" % "2.11.1",

  "log4j" % "log4j" % "1.2.17",

  "org.slf4j" % "slf4j-simple" % "1.7.25",

  "com.flipkart" % "hbase-object-mapper" % "1.3"
)
