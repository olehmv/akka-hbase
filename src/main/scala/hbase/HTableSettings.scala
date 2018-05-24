package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Scan, Row}

import scala.collection.immutable

case class HTableSettings(conf: Configuration,
                             tableName: TableName)
