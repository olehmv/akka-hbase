package akka.stream.alpakka.hbase.internal

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Get

import scala.collection.immutable

final case class HTableSettings[T](conf: Configuration,
                                   tableName: TableName,
                                   columnFamilies: immutable.Seq[String],
                                   converter: T => Get)

object HTableSettings {
  def create[T](conf: Configuration,
                tableName: TableName,
                columnFamilies: java.util.List[String],
                converter: java.util.function.Function[T, Get]): HTableSettings[T] = {
    import scala.collection.JavaConverters._
    import scala.compat.java8.FunctionConverters._
    HTableSettings(conf, tableName, immutable.Seq(columnFamilies.asScala: _*), asScalaFromFunction(converter))
  }
}
