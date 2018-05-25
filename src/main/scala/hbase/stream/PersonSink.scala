package hbase.stream

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import akka.stream.{ActorAttributes, Attributes, Inlet, SinkShape}
import com.flipkart.hbaseobjectmapper.HBObjectMapper
import hbase.connection.HBaseConnection
import hbase.entity.Person
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Put

import scala.collection.mutable

class PersonSink(configutation: Configuration,tableName: TableName) extends GraphStage[SinkShape[Person]] {

  override protected def initialAttributes: Attributes =
    Attributes.name("HBaseFLow").and(ActorAttributes.dispatcher("akka.stream.default-blocking-io-dispatcher"))

  val in: Inlet[Person] = Inlet("PersonSink")
  override val shape: SinkShape[Person] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape)with HBaseConnection {
      implicit val connection = connect(configutation)

      import collection.JavaConverters._

      val hbObjectMapper: HBObjectMapper = new HBObjectMapper
      val families: mutable.Set[String] = hbObjectMapper.getColumnFamilies(classOf[Person]).asScala
      val table = getOrCreateTable(tableName,families)

      override def preStart(): Unit = pull(in)


      setHandler(
        in,
        new InHandler {
          override def onPush(): Unit = {
            val person = grab(in)
            val put: Put = hbObjectMapper.writeValueAsPut(person)
            table.put(put)
            pull(in)

          }
        }
      )
    }
}
