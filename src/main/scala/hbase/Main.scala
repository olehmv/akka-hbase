package hbase

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import hbase.entity.Car
import hbase.stream.CarSink
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
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

   val sink : CarSink = new CarSink(HBaseConfiguration.create(),TableName.valueOf("car"))

    val f = Source(11 to 20).map(i =>new Car(i.toString, s"zozo_$i")).runWith(sink)



//    val source = new PersonSource(HTableSettings(HBaseConfiguration.create(),TableName.valueOf("person")),new Person(1.toString,"zozo_1"))
//    Source.fromGraph(source).take(1).runForeach(println)

  }


}
