package hbase.connection

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

trait HBaseConnection {

  def connect(conf: Configuration, timeout: Int = 10) =
    Await.result(Future(ConnectionFactory.createConnection(conf)), timeout seconds)

  def getOrCreateTable(tableName: TableName, columnFamilies: mutable.Set[String])(implicit connection: Connection): Table ={
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
