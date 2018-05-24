package hbase

import akka.stream.{Attributes, Inlet, SinkShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import org.apache.hadoop.hbase.client.{Connection, HTable, Put, Table}
import org.apache.hadoop.hbase.util.Bytes

class PersonSink(settings: HTableSettings[Person],person: Person) extends GraphStage[SinkShape[Person]] {
  val in: Inlet[Person] = Inlet("PersonSink")
  override val shape: SinkShape[Person] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape)with HBaseConnection {

      implicit val connection: Connection = connect(settings.conf)

      private val table: Table = getOrCreateTable(settings.tableName,settings.mapOfColumnFamileAndSeqColumns.keys.toSeq)
      implicit def stringToBytes(string: String): Array[Byte] = Bytes.toBytes(string)

      override def preStart(): Unit = pull(in)


      setHandler(
        in,
        new InHandler {
          override def onPush(): Unit = {
            val person: Person = grab(in)
            val put: Put = new Put(person.id)
            val map: Map[String, Seq[String]] = settings.mapOfColumnFamileAndSeqColumns
            map.keys.foreach(family=>map(family).foreach(column=>put.addColumn(family,column,person.name)))
            table.put(put)
            pull(in)

          }
        }
      )
    }
}
