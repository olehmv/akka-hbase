package hbase.connection

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io.compress.Compression
import org.apache.hadoop.hbase.regionserver.BloomType
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.util.bloom.BloomFilter

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

trait HBaseConnection {

  def connect(conf: Configuration, timeout: Int = 10) =
    Await.result(Future(ConnectionFactory.createConnection(conf)), timeout seconds)

  def getOrCreateTable(tableName: TableName, columnFamilies: mutable.Set[String])(implicit connection: Connection): Table = {
    val admin: Admin = connection.getAdmin

    implicit def stringTobytes(s: String) = Bytes.toBytes(s)

    if (admin.isTableAvailable(tableName)) {
      connection.getTable(tableName)
    } else {
      val tableDescriptor = new HTableDescriptor(tableName)
      tableDescriptor.setConfiguration("hbase.table.sanity.checks","false")
      columnFamilies.foreach {
        s =>
          val columnDescriptor = new HColumnDescriptor(s)
          //hfile compression algorithm
//          columnDescriptor.setCompressionType(Compression.Algorithm.SNAPPY);
          //hfile block size in kb (default is 64KB)
          columnDescriptor.setBlocksize(64);
          //enable block cache for every read operation(default is true)
          columnDescriptor.setBlockCacheEnabled(true)
          // time to life of a value based on the timestamp in seconds
          // (default is Integer.MAX_VALUE treated as live forever)
          columnDescriptor.setTimeToLive(Integer.MAX_VALUE)

          //keeps all values of colunm family in RegionServer cache
          columnDescriptor.setInMemory(true)

          // use the row key for the bloom filter(default is BloomType.NONE)
          columnDescriptor.setBloomFilterType(BloomType.ROW)

          tableDescriptor.addFamily(columnDescriptor)
      }
      admin.createTable(tableDescriptor)
      connection.getTable(tableName)
    }
  }

}
