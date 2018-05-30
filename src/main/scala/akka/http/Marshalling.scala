package akka.http

import akka.http.hbase.entity.Person
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait Marshalling extends DefaultJsonProtocol with SprayJsonSupport{

  implicit val personFormat = jsonFormat2(Person)

}
