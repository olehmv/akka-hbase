package hbase

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
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

    val source: PersonSource = new PersonSource(
      HTableSettings(HBaseConfiguration.create(),
                     TableName.valueOf("person"),
                     immutable.Map("info"->immutable.Seq("name"))),new Person("id_9","zozo_9"))

    val value: Source[Person, NotUsed] = Source.fromGraph(source)

    val sink: PersonSink = new PersonSink(HTableSettings(HBaseConfiguration.create(),TableName.valueOf("person01"),immutable.Map("info"->immutable.Seq("name"))),new Person("1","igor"))

    Source.fromGraph(source).take(1).runForeach(println)

    val f = Source(1 to 10).map(i => Person(i.toString, s"zozo_$i")).runWith(sink)
  }

  //  val tableSettings =
  //    HTableSettings(HBaseConfiguration.create(), TableName.valueOf("person"), immutable.Seq("info"), hBaseConverter)

  //    val f = Source(20 to 30).map(i => Person(i, s"zozo_$i")).via(flow).runWith(Sink.fold(0)((a, d) => a + d.id))

  //    val sink = HTableStage.sink[Person](tableSettings)
  //
//      val f = Source(1 to 10).map(i => Person(i, s"zozo_$i")).runWith(sink)


//  implicit def toBytes(string: String): Array[Byte] = Bytes.toBytes(string)

//
//  val hBaseConverter: Person => Put = { person =>
//    val put = new Put(s"id_${person.id}")
//    put.addColumn("info", "name", person.name)
//    put
//  }

}
