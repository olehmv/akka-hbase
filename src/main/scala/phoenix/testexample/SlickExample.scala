package phoenix.testexample

import java.sql.Timestamp

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl._
import eu.bitwalker.useragentutils.UserAgent
import phoenix._
import phoenix.slickflow.{Log, LogRexExp}
import slick.basic.DatabaseConfig
import slick.jdbc.meta.MTable
import slick.jdbc.{JdbcBackend, JdbcProfile}

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



  val line = "ip1 - - [24/2/2011:04:06:01 -0400] \"GET /~strabal/grease/photo9/927-3.jpg HTTP/1.1\" 200 40028 \"-\" \"Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)\""

  val log: Log = LogRexExp.parseApacheLog(line)
  val maybeTimestamp: Option[Timestamp] = getTimestamp(log.dataTime)
  val address: String = log.idAddress
  val time: Long = maybeTimestamp.get.getTime
  val request: String = log.request
  val agent = new UserAgent(log.browser)
  val browserName: String = agent.getBrowser.getName
  val operatioSystemName: String = agent.getOperatingSystem.getName

  case class Request(ip:String,time:Timestamp,request:Option[String],browser:Option[String],operationSystem:Option[String])


  class Requests(tag:Tag) extends Table[Request](tag, Some("MYSCHEMA"),"person"){

    def ip=column[String]("ip")
    def time=column[Timestamp]("timestamp",O.SqlType("ROW_TIMESTAMP"))
    def request=column[Option[String]]("request")
    def browser=column[Option[String]]("browser")
    def operationSystem=column[Option[String]]("operation_system")
    def * = (ip, time,request,browser,operationSystem) <> (Request.tupled, Request.unapply)
    def pk = primaryKey("pk_ip_time", (ip, time))
  }



  case class Person(id: String, name: Option[String])

  class Persons(tag: Tag) extends Table[Person](tag,Some("MYSCHEMA"), "person") {
    def id = column[String]("ID", O.PrimaryKey)

    def name = column[Option[String]]("NAME")

    def * = (id, name) <> (Person.tupled, Person.unapply)
  }

  class Users(tag: Tag) extends Table[(String, Option[String])](tag, Some("MYSCHEMA"),"ALPAKKA") {
    def id = column[String]("ID", O.PrimaryKey)

    def name = column[Option[String]]("NAME")

    def * = (id, name)
  }

  private val users: TableQuery[Users] = TableQuery[Users]
  val usersSchema = TableQuery[Users].schema

  private val persons: TableQuery[Persons] = TableQuery[Persons]
  val personsSchema = TableQuery[Persons].schema

  private val eventualTables: Future[Vector[MTable]] = db.run(MTable.getTables)
eventualTables.map(v=>v.foreach(m=>println(m)))
//  try {
//        Await.result(db.run(personsSchema.create), Duration.Inf)
//      } finally db.close



//  try {
//    Await.result(db.run(DBIO.seq(
//      MTable.getTables map (tables => {
//        if (!tables.exists(_.name.name == persons.baseTableRow.tableName))
//          personsSchema.create
//      })
//    )), Duration.Inf)
//  } finally db.close

  try {
    Await.result(db.run(DBIO.seq(
      MTable.getTables map (tables => {
        if (!tables.exists(_.name.name == users.baseTableRow.tableName))
          usersSchema.create
      })
    )), Duration.Inf)
  } finally db.close


  //  implicit val getUserResult = GetResult(r => Person(r.nextString(), r.nextString()))
  //  //  Slick.sink(sql"create table if not exists javatest (mykey integer not null primary key, mycolumn varchar)".as[Person])
  //
  //  // Stream the results of a query
  //  val done: Future[Done] =
  //    Slick
  //      .source(sql"select * from person where id='1'".as[Person])
  //      .log("user")
  //      .runWith(Sink.foreach(println))
  //
  //  done.onComplete {
  //    case _ =>
  //      session.close()
  //      system.terminate()
  //  }


}