package hbase

import java.util.function

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes

import scala.reflect.runtime.universe

class PersonSource(settings: HTableSettings[Person],person: Person) extends GraphStage[SourceShape[Person]]  {
  val out: Outlet[Person] = Outlet("PersonSource")
  override val shape: SourceShape[Person] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with HBaseConnection {
      // All state MUST be inside the GraphStageLogic,
      // never inside the enclosing GraphStage.
      // This state is safe to access and modify from all the
      // callbacks that are provided by GraphStageLogic and the
      // registered handlers.

      implicit val connection: Connection = connect(settings.conf)

      private val table: Table = getOrCreateTable(settings.tableName,settings.columnFamilies)

      implicit def stringToBytes(string: String): Array[Byte] = Bytes.toBytes(string)

//      implicit def bytesToInt(bytes: Array[Byte] ):Int = Bytes.toInt(bytes)

      implicit def bytesToString(bytes: Array[Byte] ):String =Bytes.toString(bytes)


      val getPerson= {
        import org.apache.hadoop.hbase.util.Bytes
        val get = new Get(Bytes.toBytes(person.id))
        get.setMaxVersions(3)
        settings.columnFamilies.foreach(c=>get.addFamily(c))
        val result: Result = table.get(get)
        val value = result.getValue(settings.columnFamilies(0),settings.columns(0))
        Person(get.getRow,value)
      }


      setHandler(
        out,
        new OutHandler {
          override def onPull(): Unit = {
            push(out, getPerson)
          }
        }
      )
    }
}