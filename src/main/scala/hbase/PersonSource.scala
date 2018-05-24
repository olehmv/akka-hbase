package hbase

import java.util.function

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import com.flipkart.hbaseobjectmapper.HBObjectMapper
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable
import scala.reflect.runtime.universe

class PersonSource(settings: HTableSettings,person: Person) extends GraphStage[SourceShape[Person]]  {
  val out: Outlet[Person] = Outlet("PersonSource")
  override val shape: SourceShape[Person] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with HBaseConnection {
      // All state MUST be inside the GraphStageLogic,
      // never inside the enclosing GraphStage.
      // This state is safe to access and modify from all the
      // callbacks that are provided by GraphStageLogic and the
      // registered handlers.

      import collection.JavaConverters._

      val hbObjectMapper: HBObjectMapper = new HBObjectMapper

      val families: mutable.Set[String] = hbObjectMapper.getColumnFamilies(classOf[Car]).asScala

      val name: TableName = settings.tableName

      implicit val connection: Connection = connect(settings.conf)

      private val table: Table = getOrCreateTable(name,families)


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