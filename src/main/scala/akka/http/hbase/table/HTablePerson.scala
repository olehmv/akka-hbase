package akka.http.hbase.table

import akka.http.hbase.entity.Person
import akka.stream.alpakka.hbase.HTableSettings
import akka.stream.alpakka.hbase.internal.HBFlowStage
import akka.stream.alpakka.hbase.scaladsl.HTableStage
import akka.stream.scaladsl.Flow
import org.apache.hadoop.hbase.client.{Get, Put}
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
  val sinkSettings =
    HTableSettings(HBaseConfiguration.create(),
      TableName.valueOf("person"),
      Seq("info"),
      personToPutConverter)
  def sink = HTableStage.sink[Person](sinkSettings)


  val personToGetConverter: Person => Get = { person =>
    implicit def toBytes(string: String): Array[Byte] = Bytes.toBytes(string)
    val id = person.id
    val name = person.name
    val get = new Get(person.id)
    get.addColumn("info", "name")
    get
  }

  val sourceSettings = akka.stream.alpakka.hbase.internal.HTableSettings(
    HBaseConfiguration.create(),
    TableName.valueOf("person"),
    Seq("info"),
    personToGetConverter)
  def flow = Flow.fromGraph(new HBFlowStage[Person](sourceSettings))

}
