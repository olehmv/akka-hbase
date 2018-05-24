package hbase

import java.lang.reflect.Field
import java.util

import akka.stream.{Attributes, Inlet, SinkShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import com.flipkart.hbaseobjectmapper.HBObjectMapper
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Connection, HTable, Put, Table}
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable

class CarSink(settings: HTableSettings) extends GraphStage[SinkShape[Car]] {
  val in: Inlet[Car] = Inlet("AvtoSink")
  override val shape: SinkShape[Car] = SinkShape(in)

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
            val car: Car = grab(in)
            val put = hbObjectMapper.writeValueAsPut(car)
            table.put(put)
            pull(in)

          }
        }
      )
    }
}

