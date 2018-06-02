package phoenix.slickflow

import java.util.regex.{Matcher, Pattern}

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
