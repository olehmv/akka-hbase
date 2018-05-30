package akka.http.hbase.table

import akka.NotUsed
import akka.http.hbase.entity.Person
import akka.stream.alpakka.hbase.HTableSettings
import akka.stream.alpakka.hbase.internal.HBF
import akka.stream.alpakka.hbase.scaladsl.HTableStage
import akka.stream.scaladsl.Flow
import org.apache.hadoop.hbase.client.{Put, ResultScanner, Scan}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}

import scala.collection.immutable.Seq

trait HTablePerson {



  val personToPutConverter: Person => Put = { person =>
    implicit def toBytes(string: String): Array[Byte] = Bytes.toBytes(string)
    val put = new Put(person.id)
    put.addColumn("info", "name", person.name)
    put
  }
  val putSettings =
    HTableSettings(HBaseConfiguration.create(),
      TableName.valueOf("person"),
      Seq("info"),
      personToPutConverter)
  def sink = HTableStage.sink[Person](putSettings)


  val personToScan: Person => Scan = { person =>
    implicit def toBytes(string: String): Array[Byte] = Bytes.toBytes(string)
    val id = person.id
    val name = person.name
    val scan = new Scan()
    if(id.length!=0){
      scan.setStartRow(id);scan.setStopRow(id)
    }
    scan.addColumn("info", "name")
    scan
  }

  val getSettings = akka.stream.alpakka.hbase.internal.HTableSettings(
    HBaseConfiguration.create(),
    TableName.valueOf("person"),
    Seq("info"),
    personToScan)
  def flow = Flow.fromGraph(new HBF[Person](getSettings))



   val scan: Flow[Person, ResultScanner, NotUsed] = Flow.fromGraph(new HBF[Person](getSettings))



}
