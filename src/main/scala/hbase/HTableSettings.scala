package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Scan, Row}

import scala.collection.immutable

case class HTableSettings[T](conf: Configuration,
                             tableName: TableName,
                             columnFamilies: immutable.Seq[String],
                             columns: immutable.Seq[String])
