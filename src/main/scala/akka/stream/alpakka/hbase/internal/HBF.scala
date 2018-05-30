package akka.stream.alpakka.hbase.internal

import akka.stream._
import akka.stream.stage._
import org.apache.hadoop.hbase.client.{ResultScanner, Scan, Table}

import scala.util.control.NonFatal

class HBF[A](
    settings: akka.stream.alpakka.hbase.internal.HTableSettings[A])
    extends GraphStage[FlowShape[A, ResultScanner]] {

  override protected def initialAttributes: Attributes =
    Attributes
      .name("HBaseFLow")
      .and(
        ActorAttributes.dispatcher(
          "akka.stream.default-blocking-io-dispatcher"))

  private val in = Inlet[A]("messages")
  private val out = Outlet[ResultScanner]("result")

  override val shape = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with StageLogging with HBaseCapabilities {

      override protected def logSource = classOf[HBF[A]]

      implicit val connection = connect(settings.conf)

      lazy val table: Table =
        getOrCreateTable(settings.tableName, settings.columnFamilies).get

      setHandler(out, new OutHandler {
        override def onPull() = {
          pull(in)
        }
      })

      setHandler(
        in,
        new InHandler {
          override def onPush(): Unit = {
            val a = grab(in)
            val get = settings.converter(a)
            val scan = new Scan(get)
            val resultScanner = table.getScanner(scan)
            push(out, resultScanner)

          }
        }
      )

      override def postStop() = {
        log.debug("Stage completed")
        try {
          table.close()
          log.debug("table closed")
        } catch {
          case NonFatal(ex) =>
            log.error(ex, "Problem occurred during producer table close")
        }
        try {
          connection.close()
          log.debug("connection closed")
        } catch {
          case NonFatal(ex) =>
            log.error(ex, "Problem occurred during producer connection close")
        }
      }
    }

}
