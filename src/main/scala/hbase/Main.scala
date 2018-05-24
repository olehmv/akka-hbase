package hbase

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.alpakka.hbase.scaladsl.HTableStage
import akka.stream.scaladsl.{Sink, Source}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}

import scala.collection.immutable
object Main {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()


//    val value: Source[Person, NotUsed] = Source.fromGraph(source)
//    Source.fromGraph(source).take(1).runForeach(println)
//
//    val sink:PersonSink=new PersonSink(HTableSettings(HBaseConfiguration.create(),TableName.valueOf("person")))
//
//    val f = Source(11 to 20).map(i => new Person(i.toString, s"zozo_$i")).runWith(sink)

//   val sink : CarSink = new CarSink(HTableSettings(HBaseConfiguration.create(),
//      TableName.valueOf("car")))
//
//    val f = Source(1 to 10).map(i =>new Car(i.toString, s"zozo_$i")).runWith(sink)

    val source = new PersonSource(HTableSettings(HBaseConfiguration.create(),TableName.valueOf("person")),new Person(1.toString,"zozo_1"))
    Source.fromGraph(source).take(1).runForeach(println)

  }


}
