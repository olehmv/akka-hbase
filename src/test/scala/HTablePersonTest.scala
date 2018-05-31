import akka.http.hbase.entity.Person
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.{HBApi, Marshalling}
import org.scalatest._
import spray.json._

class HTablePersonTest
    extends FlatSpec
    with Matchers
    with ScalatestRouteTest
    with Marshalling {


  val api = new HBApi()

   val testPerson = Person("test", "test")


  "HTable Person" should "respond post request" in {
    Post(s"/persons",testPerson) ~> api.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Person] shouldBe testPerson

    }
  }

  "HTable Person" should "respond to get request" in {
    Get(s"/persons/${testPerson.id}") ~> api.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[String].replace("[","").replace("]","") shouldBe testPerson.toJson.toString()
    }
  }

}
