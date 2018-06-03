import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Pattern

import proxy.dto.Request

import scala.util.{Failure, Success, Try}

package object proxy {

  def getTimestamp(s: String): Option[Timestamp] = s match {
    case "" => None
    case _ => {
      val format = new SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ss")
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
    def parseRequest(line: String): Request = {
      val matcher = PATTERN.matcher(line)
      if (!matcher.matches) throw new IllegalArgumentException
      new Request(matcher.group(1).toInt, getTimestamp(matcher.group(4)).get,Option(matcher.group(5)), Option(matcher.group(6)),Option(matcher.group(7)))
    }

  }

}
