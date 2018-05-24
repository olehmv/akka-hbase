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

      private val table: Table = getOrCreateTable(settings.tableName,settings.mapOfColumnFamileAndSeqColumns.keys.toSeq)

      implicit def stringToBytes(string: String): Array[Byte] = Bytes.toBytes(string)

//      implicit def bytesToInt(bytes: Array[Byte] ):Int = Bytes.toInt(bytes)

      implicit def bytesToString(bytes: Array[Byte] ):String =Bytes.toString(bytes)


      val getPerson= {
        import org.apache.hadoop.hbase.util.Bytes
        val get = new Get(Bytes.toBytes(person.id))
//        get.setMaxVersions(3)
        settings.mapOfColumnFamileAndSeqColumns match {
          case map:  Map[String, scala.Seq[String]] => map.keys.foreach(family=>map(family).foreach(coulmn=>get.addColumn(family,coulmn)))

        }
        val result: Result = table.get(get)
        val family:String = settings.mapOfColumnFamileAndSeqColumns.keys.toList(0)
        val column:String = settings.mapOfColumnFamileAndSeqColumns(family)(0)
        val value:String = result.getValue(family,column)
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