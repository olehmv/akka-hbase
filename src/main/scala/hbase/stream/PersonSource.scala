package hbase.stream

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{ActorAttributes, Attributes, Outlet, SourceShape}
import com.flipkart.hbaseobjectmapper.HBObjectMapper
import hbase.connection.HBaseConnection
import hbase.entity.Person
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

import scala.collection.mutable

class PersonSource(configutation: Configuration,tableName: TableName,person: Person) extends GraphStage[SourceShape[Person]]  {
  override protected def initialAttributes: Attributes =
    Attributes.name("HBaseFLow").and(ActorAttributes.dispatcher("akka.stream.default-blocking-io-dispatcher"))

  val out: Outlet[Person] = Outlet("PersonSource")
  override val shape: SourceShape[Person] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with HBaseConnection {
      // All state MUST be inside the GraphStageLogic,
      // never inside the enclosing GraphStage.
      // This state is safe to access and modify from all the
      // callbacks that are provided by GraphStageLogic and the
      // registered handlers.
      implicit val connection = connect(configutation)

      import collection.JavaConverters._

      val hbObjectMapper: HBObjectMapper = new HBObjectMapper
      val families: mutable.Set[String] = hbObjectMapper.getColumnFamilies(classOf[Person]).asScala


      val table = getOrCreateTable(tableName,families)


      setHandler(
        out,
        new OutHandler {
          override def onPull(): Unit = {
            val writable: ImmutableBytesWritable = hbObjectMapper.getRowKey(person)
            val put = hbObjectMapper.writeValueAsPut(person)
            val result: Person = hbObjectMapper.readValue(writable,put,classOf[Person])
            push(out, result)
          }
        }
      )
    }
}