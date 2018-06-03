package proxy.dto

import java.sql.Timestamp

case class Request(ip: Int, time: Timestamp, request: Option[String], browser: Option[String], operationSystem: Option[String])
