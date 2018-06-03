package proxy.dao

import java.sql.Connection


object PhoenixDAO{
  def apply(connection: Connection): PhoenixDAO = new PhoenixDAO(connection)
}
class PhoenixDAO(val connection: Connection) {
  def createTable(sql:String): Unit ={
    val statement = connection.createStatement()
    statement.executeUpdate(sql.toUpperCase)
  }
}
