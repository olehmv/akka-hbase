package akka.http

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.stream.alpakka.hbase.HTableSettings
import akka.stream.alpakka.hbase.internal.HBFlowStage
import akka.stream.alpakka.hbase.scaladsl.HTableStage
import akka.stream.scaladsl.{Flow, Source}
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.hbase.client.{Get, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}

import scala.collection.immutable.Seq
import scala.concurrent.Future

object Api extends App with Marshalling {
  implicit val system = ActorSystem("person")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  def routes: Route = postRoute ~ getRoute
  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(routes, host, port)

  val log = Logging(system.eventStream, "logs")
  bindingFuture
    .map { serverBinding =>
      log.info(s"Bound to ${serverBinding.localAddress} ")
    }
    .onFailure {
      case ex: Exception =>
        log.error(ex, "Failed to bind to {}:{}!", host, port)
        system.terminate()
    }

  def postRoute =
    pathPrefix("person") {
      pathEndOrSingleSlash {
        post {
          entity(as[Person]) { person =>
            Source.single(person).runWith(sink)
            complete {
              s"person was added $person"
            }
          }
        }
      }
    }
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport
      .json()
      .withParallelMarshalling(parallelism = 8, unordered = false)

  def getRoute =
    pathPrefix("persons"/ Segment) { id=>
      pathEndOrSingleSlash {
        get {
          complete{
            val marshallable: ToResponseMarshallable = Source.single(Person(id,"")).via(f)
              marshallable
          }
        }
      }
    }

  val hBaseConverter: Person => Put = { person =>
    implicit def toBytes(string: String): Array[Byte] = Bytes.toBytes(string)
    val put = new Put(s"id_${person.id}")
    put.addColumn("info", "name", person.name)
    put
  }

  val tableSettings =
    HTableSettings(HBaseConfiguration.create(),
                   TableName.valueOf("person"),
                   Seq("info"),
                   hBaseConverter)


  def sink = HTableStage.sink[Person](tableSettings)
  def flow = HTableStage.flow[Person](tableSettings)

  val convert: Person => Get = { person =>
    implicit def toBytes(string: String): Array[Byte] = Bytes.toBytes(string)
    val get = new Get(person.id)
    get
  }

  val settings = akka.stream.alpakka.hbase.internal.HTableSettings(HBaseConfiguration.create(),
                                  TableName.valueOf("person"),
                                  Seq("info"),
                                  convert)

  def f =Flow.fromGraph(new HBFlowStage[Person](settings))

}
