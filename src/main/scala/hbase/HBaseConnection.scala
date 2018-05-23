package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client.{Admin, Connection, ConnectionFactory, Table,Get}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

import scala.concurrent.ExecutionContext.Implicits.global


trait HBaseConnection {

  def connect(conf: Configuration, timeout: Int = 10) =
    Await.result(Future(ConnectionFactory.createConnection(conf)), timeout seconds)

  def getOrCreateTable(tableName: TableName, columnFamilies: Seq[String])(implicit connection: Connection): Table ={
    val admin: Admin = connection.getAdmin
    if(admin.isTableAvailable(tableName)){
      connection.getTable(tableName)
    }else{
      val tableDescriptor = new HTableDescriptor(tableName)
      columnFamilies.foreach(s=>tableDescriptor.addFamily(new HColumnDescriptor(s)))
      admin.createTable(tableDescriptor)
      connection.getTable(tableName)
    }

  }

}
