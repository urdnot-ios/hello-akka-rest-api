package com.urdnot.api

import akka.Done
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.testkit.scaladsl.TestcontainersKafkaLike
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.Inside

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps


class IntegrationSpec extends SpecBase with TestcontainersKafkaLike with Inside {
//  override val testcontainersSettings: KafkaTestkitTestcontainersSettings = KafkaTestkitTestcontainersSettings(system)
//    .withNumBrokers(3)
//    .withInternalTopicsReplicationFactor(2)
//    .withConfigureKafka { brokerContainers =>
//      brokerContainers.foreach(_.withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true"))
//    }

  implicit val patience: PatienceConfig = PatienceConfig(10.seconds, 500.millis)
  setUp()


  // val config = system.settings.config.getConfig("akka.kafka.producer")
  val producerSettings: ProducerSettings[String, String] = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)
  val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)

    "Kafka producer" must {
    "produce to plainSink" in {
        val topic1 = createTopic(1)
        val group1 = createGroupId(1)
        Await.result(Producers.produce (topic1, producerSettings), remainingOrDefault)
        val (control, probe) = Consumer
          .plainSource(consumerDefaults.withGroupId(group1), Subscriptions.topics(topic1))
          .map{x =>x.value()}
          .toMat(TestSink.probe)(Keep.both)
          .run()
        probe
          .request(100)
          .expectNextN(100)
        val stopped = control.stop()
        probe.expectComplete()

        Await.result(stopped, remainingOrDefault)
        control.shutdown()
        probe.cancel()
    }
  }
  "Kafka consumer" must {
    "consume from a Kafka Source" in {
      // Build a producer
      val topic = createTopic(1)
      val done: Future[Done] =
        Source(1 to 100)
          .map(_.toString)
          .map(value => new ProducerRecord[String, String](topic, value))
          .runWith(Producer.plainSink(producerSettings))

      // Run the consumer, verify it consumed what you produced

      val consumerSource =  Consumers.consume(topic, consumerSettings.withBootstrapServers(bootstrapServers))
      val future = consumerSource.take(10).runWith(Sink.seq)
      val result = Await.result(future, 3.seconds)
      assert(result.toList == List.range(1, 11))
    }
  }
}
