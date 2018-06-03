package proxy.phoenix

import java.sql.{Connection, DriverManager}
import java.util.concurrent.Executors

import com.typesafe.config.ConfigFactory

object PhoenixConnection {


  val config = ConfigFactory.load()
  val uri: String = config.getString("phoenix.url")

  def connect: Connection = {
    val connection = DriverManager.getConnection(uri)
    connection.setNetworkTimeout(Executors.newSingleThreadExecutor(), 100000)
    connection
  }

}
