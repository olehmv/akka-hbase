package study

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Graph, SourceShape}

import scala.concurrent.Future

object StudyAkkaStream {

  import java.security.MessageDigest

  import akka.stream.scaladsl.Source
  import akka.stream.stage._
  import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
  import akka.util.ByteString

  def main(args: Array[String]): Unit = {
    val data: Source[ByteString, NotUsed] = Source.single(ByteString("abc"))
    val digest: Source[ByteString, NotUsed] =
      data.via(new DigestCalculator("SHA-256"))

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    // A GraphStage is a proper Graph, just like what GraphDSL.create would return
    val sourceGraph: Graph[SourceShape[Int], NotUsed] = new NumbersSource

    // Create a Source from the Graph to access the DSL
    val mySource: Source[Int, NotUsed] = Source.fromGraph(sourceGraph)

    // Returns 55
    val result1: Future[Int] = mySource.take(10).runFold(0)(_ + _)

    // The source is reusable. This returns 5050
    val result2: Future[Int] = mySource.take(100).runFold(0)(_ + _)

    mySource.take(10).runForeach(println)

  }

  class DigestCalculator(algorithm: String)
      extends GraphStage[FlowShape[ByteString, ByteString]] {
    val in = Inlet[ByteString]("DigestCalculator.in")
    val out = Outlet[ByteString]("DigestCalculator.out")
    override val shape = FlowShape(in, out)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) {
        private val digest = MessageDigest.getInstance(algorithm)
//      When the stream starts, the onPull handler of the stage is called, which just bubbles up the pull event to its upstream
        setHandler(out, new OutHandler {
          override def onPull(): Unit = pull(in)
        })
//      As a response to this pull, a ByteString chunk will arrive (onPush) which we use to update the digest, then it will pull for the next chunk.
        setHandler(
          in,
          new InHandler {
            override def onPush(): Unit = {
              val chunk = grab(in)
              digest.update(chunk.toArray)
              pull(in)
            }

            override def onUpstreamFinish(): Unit = {
              emit(out, ByteString(digest.digest()))
              completeStage()
            }
          }
        )
      }
  }

  import akka.stream.SourceShape
  import akka.stream.stage.{GraphStage, OutHandler}

  class NumbersSource extends GraphStage[SourceShape[Int]] {
    val out: Outlet[Int] = Outlet("NumbersSource")
    override val shape: SourceShape[Int] = SourceShape(out)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) {
        // All state MUST be inside the GraphStageLogic,
        // never inside the enclosing GraphStage.
        // This state is safe to access and modify from all the
        // callbacks that are provided by GraphStageLogic and the
        // registered handlers.
        private var counter = 1

        setHandler(
          out,
          new OutHandler {
//          override def onDownstreamFinish(){}
            override def onPull(): Unit = {
//              isAvailable(out)
//              isClosed(out)
//              complete(out)
//              fail(out, exception)
              push(out, counter)
              counter += 1
            }
          }
        )
      }
  }

  import akka.stream.SinkShape
  import akka.stream.stage.{GraphStage, InHandler}

  class StdoutSink extends GraphStage[SinkShape[Int]] {
    val in: Inlet[Int] = Inlet("StdoutSink")
    override val shape: SinkShape[Int] = SinkShape(in)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) {

        // This requests one element at the Sink startup.
        override def preStart(): Unit = pull(in)

        setHandler(
          in,
          new InHandler {
//          override def onUpstreamFinish(){}
//          override def onUpstreamFailure(ex: Throwable){}
            override def onPush(): Unit = {
//          isAvailable(in)
//          cancel(in)
//          hasBeenPulled(in)
//          isClosed(in)
              println(grab(in))
//            request the next element
              pull(in)
            }
          }
        )
      }
  }

}
