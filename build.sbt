name := "akka-hbase"

version := "0.1"

scalaVersion := "2.11.1"

val akkaVersion = "2.4.12"

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/com.lightbend.akka/akka-stream-alpakka-hbase
  "com.lightbend.akka" %% "akka-stream-alpakka-hbase" % "0.19",
  // https://mvnrepository.com/artifact/log4j/log4j
  // https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream
 // "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  // https://mvnrepository.com/artifact/org.scala-lang/scala-reflect
  "org.scala-lang" % "scala-reflect" % "2.11.1",

"log4j" % "log4j" % "1.2.17",
  // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
  "org.slf4j" % "slf4j-simple" % "1.7.25"
)
