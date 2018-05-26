package hbase.stream

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import com.flipkart.hbaseobjectmapper.HBObjectMapper
import hbase.connection.HBaseConnection
import hbase.entity.Car
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName

import scala.collection.mutable

class CarSink(configutation: Configuration,tableName: TableName) extends GraphStage[SinkShape[Car]] {
  val in: Inlet[Car] = Inlet("AvtoSink")

  override val shape: SinkShape[Car] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with HBaseConnection {
      implicit val connection = connect(configutation)
      import collection.JavaConverters._

      val hbObjectMapper: HBObjectMapper = new HBObjectMapper
      val families: mutable.Set[String] = hbObjectMapper.getColumnFamilies(classOf[Car]).asScala
      val table = getOrCreateTable(tableName,families)


      override def preStart(): Unit = pull(in)

      setHandler(
        in,
        new InHandler {
          override def onPush(): Unit = {
            val car: Car = grab(in)
            val put = hbObjectMapper.writeValueAsPut(car)
            table.put(put)
            table.close()
            pull(in)
          }
        }
      )
    }
}

