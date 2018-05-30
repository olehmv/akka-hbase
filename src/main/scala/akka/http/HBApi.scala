package akka.http

import akka.Done
import akka.http.hbase.entity.Person
import akka.http.hbase.table.HTablePerson
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Source}
import org.apache.hadoop.hbase.util.Bytes

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class HBApi(
             implicit val executionContext: ExecutionContext,
             val materializer: ActorMaterializer
           ) extends Marshalling
  with HTablePerson {

  def routes = postRoute ~ getRoute

  def postRoute =
    pathPrefix("persons") {
      pathEndOrSingleSlash {
        post {
          entity(as[Person]) { person =>
            val eventualDone: Future[Done] = Source.single(person).toMat(sink)(Keep.right).run()
            onComplete(
              eventualDone
            ) {
              case Success(Done) => {
                complete(StatusCodes.OK, person)
              }
              case Failure(e) =>
                complete(
                  StatusCodes.BadRequest
                )
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
    pathPrefix("persons" / Segment) { id =>
      pathEndOrSingleSlash {
        get {
          complete {
            val marshallable =
              Source.single(Person(id, "")).via(flow).map[Person] { res =>
                val value = Bytes.toString(res.value())
                val key: String = Bytes.toString(res.getRow)
                Person(key, value)
              }
            marshallable
          }
        }
      }

    }

  pathPrefix("persons") { id =>
    pathEndOrSingleSlash {
      get {

      }
    }

  }


}
