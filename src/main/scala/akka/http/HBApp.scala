package akka.http

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

object HBApp extends App{

  implicit val system = ActorSystem("person")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  private val routes = new HBApi().routes

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

}
