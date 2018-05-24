package hbase

import akka.stream.{Attributes, Inlet, SinkShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import com.flipkart.hbaseobjectmapper.HBObjectMapper
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Connection, HTable, Put, Table}
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable

class PersonSink(settings: HTableSettings) extends GraphStage[SinkShape[Person]] {
  val in: Inlet[Person] = Inlet("PersonSink")
  override val shape: SinkShape[Person] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape)with HBaseConnection {

      import collection.JavaConverters._

      val hbObjectMapper: HBObjectMapper = new HBObjectMapper

      val families: mutable.Set[String] = hbObjectMapper.getColumnFamilies(classOf[Car]).asScala

      val name: TableName = settings.tableName

      implicit val connection: Connection = connect(settings.conf)

      private val table: Table = getOrCreateTable(name,families)

      override def preStart(): Unit = pull(in)


      setHandler(
        in,
        new InHandler {
          override def onPush(): Unit = {
            val person = grab(in)
            val put = hbObjectMapper.writeValueAsPut(person)
            table.put(put)
            pull(in)

          }
        }
      )
    }
}
