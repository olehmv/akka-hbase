package phoenix.slickflow

import java.sql.Timestamp

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.{Sink, Source}
import eu.bitwalker.useragentutils.UserAgent
import phoenix.getTimestamp
import phoenix.testexample.SlickExample.{Users, db, persons, personsSchema, session}
import slick.compiler.QueryCompiler
import slick.jdbc.JdbcBackend
import slick.jdbc.meta.MTable

import scala.collection.immutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object RequestFlow extends App {

  val line = "ip1 - - [24/2/2011:04:06:01 -0400] \"GET /~strabal/grease/photo9/927-3.jpg HTTP/1.1\" 200 40028 \"-\" \"Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)\""

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher
  implicit val session = SlickSession.forConfig("slick-h2-phoenix")
  private val db: JdbcBackend#DatabaseDef = session.db

  import session.profile.api._

  case class Request(ip: String, time: Option[Timestamp], request: Option[String], browser: Option[String], operationSystem: Option[String])


  class Requests(tag: Tag) extends Table[Request](tag, Some("M"),"REQUEST") {

    def ip = column[String]("ip", O.PrimaryKey)

    def time = column[Option[Timestamp]]("timestamp")

    def request = column[Option[String]]("request")

    def browser = column[Option[String]]("browser")

    def operationSystem = column[Option[String]]("operation_system")

    def * = (ip, time, request, browser, operationSystem) <> (Request.tupled, Request.unapply)

  }

  val requests: TableQuery[Requests] = TableQuery[Requests]
  val requestsSchema = TableQuery[Requests].schema

  val eventualTables: Future[Vector[MTable]] = db.run(MTable.getTables)

//  try {
//    Await.result(db.run(
//          requestsSchema.create), Duration.Inf)
//  } finally db.close

//  try {
//    Await.result(db.run(DBIO.seq(
//      MTable.getTables map (tables => {
//        if (!tables.exists(_.name.name == requests.baseTableRow.tableName))
//          requestsSchema.create
//      })
//    )), Duration.Inf)
//  } finally db.close


  val log: Log = LogRexExp.parseApacheLog(line)
  val maybeTimestamp: Option[Timestamp] = getTimestamp(log.dataTime)
  val address: String = log.idAddress
  val time: Long = maybeTimestamp.get.getTime
  val request: String = log.request
  val agent = new UserAgent(log.browser)
  val browserName: String = agent.getBrowser.getName
  val operatioSystemName: String = agent.getOperatingSystem.getName
  val requestsS: immutable.IndexedSeq[Request] = (1 to 10).map(i => Request(i.toString, maybeTimestamp, Option(request), Option(browserName), Option(operatioSystemName)))
//  Slick.sink(request => sqlu"UPSERT INTO M.REQUEST VALUES(${request.ip}, ${request.time},${request.request},${request.browser},${request.operationSystem})")
  private val eventualDone: Future[Done] = Source(requestsS).runWith(Sink.foreach(request=>sql"UPSERT INTO M.REQUEST VALUES(${request.ip}, ${request.time},${request.request},${request.browser},${request.operationSystem})"))

  eventualDone.onComplete {
    case _ =>
      session.close()
      system.terminate()
  }

//  val done: Future[Done] =
//    Source(requestsS)
//      .runWith(
//        // add an optional first argument to specify the parallism factor (Int)
//        Slick.sink(request => sqlu"UPSERT INTO M.REQUEST VALUES(${request.ip}, ${request.time},${request.request},${request.browser},${request.operationSystem})")
//      )
//
//  done.onComplete {
//    case _ =>
//      session.close()
//      system.terminate()
//  }

}
