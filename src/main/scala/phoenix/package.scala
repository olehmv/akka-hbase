import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Pattern
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
  object LogRexExp {

    private val LOGENTRYPATTERN = "(ip\\d+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"([^\"]+)\""
    private val PATTERN = Pattern.compile(LOGENTRYPATTERN)

    /**
      * Parses log line, throws IllegalArgumentException if finds bad log line
      *
      * @param String -> line
      * @return Log -> POJO holder for log properties
      * @throws IllegalArgumentException
      */
    @throws[IllegalArgumentException]
    def parseApacheLog(line: String): Log = {
      val matcher = PATTERN.matcher(line)
      if (!matcher.matches) throw new IllegalArgumentException
      new Log(matcher.group(1), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7), matcher.group(9))
    }

  }
  case class Log(idAddress: String,dataTime:String,request:String,response:String,byteSent:String,browser:String)

}
