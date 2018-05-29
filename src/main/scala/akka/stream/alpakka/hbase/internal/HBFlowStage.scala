package akka.stream.alpakka.hbase.internal

import akka.stream._
import akka.stream.stage._
import org.apache.hadoop.hbase.client.Table

import scala.util.control.NonFatal

class HBFlowStage[A](
    settings: akka.stream.alpakka.hbase.internal.HTableSettings[A])
    extends GraphStage[FlowShape[A, A]] {

  override protected def initialAttributes: Attributes =
    Attributes
      .name("HBaseFLow")
      .and(
        ActorAttributes.dispatcher(
          "akka.stream.default-blocking-io-dispatcher"))

  private val in = Inlet[A]("messages")
  private val out = Outlet[A]("result")

  override val shape = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with StageLogging with HBaseCapabilities {



      override protected def logSource = classOf[HBFlowStage[A]]

      implicit val connection = connect(settings.conf)

      lazy val table: Table =
        getOrCreateTable(settings.tableName, settings.columnFamilies).get

      setHandler(out, new OutHandler {
        override def onPull() = {

        }
      })

      setHandler(in, new InHandler {
        override def onPush(): Unit = {

        }
      })

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
