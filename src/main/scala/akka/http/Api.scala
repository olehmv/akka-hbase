package akka.http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.directives.PathDirectives._
import akka.stream.ActorMaterializer
import akka.stream.alpakka.hbase.HTableSettings
import akka.stream.alpakka.hbase.scaladsl.HTableStage
import akka.stream.scaladsl.{Flow, Source}
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.immutable.Seq
import scala.concurrent.Future

class Api extends Marshalling {
  implicit val system= ActorSystem("person")
  implicit val materializer = ActorMaterializer()


  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  def routes: Route = postRoute
  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(postRoute, host, port)

  val log =  Logging(system.eventStream, "logs")
  bindingFuture.map { serverBinding =>
    log.info(s"Bound to ${serverBinding.localAddress} ")
  }.onFailure {
    case ex: Exception =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }

  def postRoute =
    pathPrefix("person") {
      pathEndOrSingleSlash{
        post{
          entity(as[Person]){person=>
            Source.single[Person](person).to(sink)
            complete {
              s"<h1>person was added $person</h1>"
            }
          }
        }

      }

    }

  def getRoute =
    pathPrefix("person"/Segment) {personId=>
      pathEndOrSingleSlash {
        get {
          complete {

            null
          }
        }


      }

    }


  implicit def toBytes(string: String): Array[Byte] = Bytes.toBytes(string)

  val hBaseConverter: Person => Put = { person =>
    val put = new Put(s"id_${person.id}")
    put.addColumn("info", "name", person.name)
    put
  }
  val tableSettings =
    HTableSettings(HBaseConfiguration.create(), TableName.valueOf("person"), Seq("info"), hBaseConverter)

  def sink = HTableStage.sink[Person](tableSettings)

}
