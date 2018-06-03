package proxy


import java.sql.Timestamp

import proxy.dao.{PhoenixDAO, RequestDAO}
import proxy.dto.Request
import proxy.phoenix.PhoenixConnection

object M extends App {

  val connection: java.sql.Connection = PhoenixConnection.connect

  val phoenixDAO = PhoenixDAO(connection)

  val tableName = "proxy.request"
  val sql = s"create table if not exists $tableName (" +
    s"ip integer not null," +
    s"time timestamp not null," +
    s"request varchar," +
    s"browser varchar," +
    s"operation_system varchar," +
    s"constraint pk primary key (ip,time row_timestamp)" +
    s")"
  val requestDAO = RequestDAO(sql, tableName, phoenixDAO)
  requestDAO.createTable
  val request = Request(11,getTimestamp("2002-05-30T09:30:10.5").get,Option("get"),Option("g"),Option("w"))
  requestDAO.addRequest(request)
  val result: Request = requestDAO.getRequest(request.copy(ip=12))
  println(result)
}
