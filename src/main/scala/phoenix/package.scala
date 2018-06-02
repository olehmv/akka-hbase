import java.sql.Timestamp
import java.text.SimpleDateFormat

import scala.util.{Failure, Success, Try}

package object phoenix {

  def getTimestamp(s: String): Option[Timestamp] = s match {
    case "" => None
    case _ => {
      val format = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ssZ")
      Try(new Timestamp(format.parse(s).getTime)) match {
        case Success(t) => Some(t)
        case Failure(_) => None
      }
    }
  }

}
