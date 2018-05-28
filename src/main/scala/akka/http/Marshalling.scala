package akka.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait Marshalling extends DefaultJsonProtocol with SprayJsonSupport{

  implicit val logIdFormat = jsonFormat2(Person)

}
