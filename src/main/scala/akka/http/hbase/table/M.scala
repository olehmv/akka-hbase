package akka.http.hbase.table

import akka.actor.ActorSystem
import akka.http.hbase.entity.Person
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.hadoop.hbase.util.Bytes

object M extends HTablePerson {

  implicit val system = ActorSystem("person")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]): Unit = {

    implicit def bytesToString(bytes: Array[Byte]) = Bytes.toString(bytes)

    Source.single(Person("", "")).via(scan).map[Array[Person]](res => {
      val r = res.next(10)
      r.map(r=> Person(r.getRow, r.value()))
    }
    ).runWith(Sink.foreach(t=>t.foreach(t=>println(t))))
  }
}
