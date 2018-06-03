package phoenix.testexample

import java.sql.Timestamp

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.Config
import eu.bitwalker.useragentutils.UserAgent
import javax.security.auth.login.Configuration
import phoenix._
import phoenix.testexample.SlickExample.session
import slick.basic.DatabaseConfig
import slick.jdbc.meta.MTable
import slick.jdbc.{GetResult, JdbcBackend, JdbcProfile}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object SlickExample extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher
  implicit val session = SlickSession.forConfig("slick-h2-phoenix")
  private val db: JdbcBackend#DatabaseDef = session.db
  val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("slick-h2-phoenix")

  import session.profile.api._


//
//   case class Person(id: String, name: Option[String])
//
//   val personsSchema: session.profile.SchemaDescription = TableQuery[Persons].schema
//
//    class Persons(tag: Tag) extends Table[Person](tag, Some("MYSCHEMA"), "PERSON") {
//      def id = column[String]("ID", O.PrimaryKey)
//
//      def name = column[Option[String]]("NAME")
//
//      def * = (id, name) <> (Person.tupled, Person.unapply)
//    }
//    try {
//      Await.result(db.run(personsSchema.create), Duration.Inf)
//    } finally db.close
//
//
//
//    private val value: SimpleDBIO[Boolean] = SimpleDBIO[Boolean](_.connection.getAutoCommit)
//    case class User(id: Int, name: String)
//    val users = (1 to 42).map(i => User(i, s"Name$i"))
//
//    val done: Future[Done] =
//      Source(users)
//        .runWith(
//          // add an optional first argument to specify the parallism factor (Int)
//          Slick.sink(user => sqlu"UPSERT INTO R.JAVATEST VALUES(${user.id}, ${user.name})".transactionally)
//        )
//
//    done.onComplete {
//      case a=>
//        val aa=a
//        session.profile.backend.synchronized()
//        session.close()
//        system.terminate()
//    }

  case class Request(ip: Int, time: Timestamp, request: Option[String], browser: Option[String], operationSystem: Option[String])

//  class Requests(tag: Tag) extends Table[Request](tag, Some("PROXY"), "REQUEST") {
//
//    def ip = column[Int]("IP")
//
//    def time = column[Timestamp]("TIME")
//
//    def request = column[Option[String]]("REQUEST")
//
//    def browser = column[Option[String]]("BROWSER")
//
//    def operationSystem = column[Option[String]]("OPERATION_SYSTEM")
//
//    def pk = primaryKey("pk", (ip, time))
//
//    def * = (ip, time, request, browser, operationSystem) <> (Request.tupled, Request.unapply)
//  }
//
//  val doneR: Future[Done] =
//    Slick
//      .source(TableQuery[Requests].result)
//      .log("user")
//      .runWith(Sink.foreach(re=>println(re)))
//
//  doneR.onComplete {
//    case _ =>
//      session.close()
//      system.terminate()
//  }

   implicit val value = GetResult(r=>Request(r.nextInt(),r.nextTimestamp(),Option(r.nextString()),Option(r.nextString()),Option(r.nextString())))


  val done: Future[Done] ={
    Slick
      .source(sql"SELECT * FROM PROXY.REQUEST WHERE IP=1".as[Request])
      .runWith(Sink.foreach(
        r=>
          println(r)))
      }


  done.onComplete {
    case _ =>
      session.close()
      system.terminate()
  }


  }