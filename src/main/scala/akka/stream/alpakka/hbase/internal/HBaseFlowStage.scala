package akka.stream.alpakka.hbase.internal


case class HBaseFlowStage[T](hBaseFlowStage: HBaseFlowStage[T] = classOf[HBaseFlowStage[T]].newInstance())

