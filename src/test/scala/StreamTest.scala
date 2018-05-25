import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.testkit.TestPublisher
import akka.stream.testkit.scaladsl.TestSource
import akka.testkit.{TestKit, TestProbe}
import hbase.entity.{Car, Person}
import hbase.stream.{CarSink, CarSource, PersonSink, PersonSource}
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class StreamTest
    extends TestKit(ActorSystem("InventoryTest"))
    with WordSpecLike
    with BeforeAndAfterAll
    with MustMatchers {
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    system.terminate()
  }

  "CarStream" must {
    val probe = TestProbe()

    "sink data into hbase table" in {
      val sink : CarSink = new CarSink(HBaseConfiguration.create(),TableName.valueOf("car"))
      val value: TestPublisher.Probe[Car] = TestSource.probe[Car].toMat(sink)(Keep.left).run()
      value.sendNext(new Car("test","test")).ensureSubscription()
    }

    "source data from hbase table" in {
      val source: CarSource = new CarSource(HBaseConfiguration.create(),TableName.valueOf("car"),new Car("test","test"))
      val cancellable:NotUsed = Source.fromGraph(source).to(Sink.actorRef(probe.ref, "completed")).run()
      probe.expectMsgAllClassOf(classOf[Car])
    }


  }
  "PersonStream" must {
    val probe = TestProbe()

    "sink data into hbase table" in {
      val sink : PersonSink = new PersonSink(HBaseConfiguration.create(),TableName.valueOf("person"))
      val value: TestPublisher.Probe[Person] = TestSource.probe[Person].toMat(sink)(Keep.left).run()
      value.sendNext(new Person("test","test")).ensureSubscription()
    }

    "source data from hbase table" in {
      val source: PersonSource = new PersonSource(HBaseConfiguration.create(),TableName.valueOf("person"),new Person("test","test"))
      val cancellable:NotUsed = Source.fromGraph(source).to(Sink.actorRef(probe.ref, "completed")).run()
      probe.expectMsgAllClassOf(classOf[Person])
    }

  }

}
