package com.urdnot.api

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.stream.scaladsl.Sink
import com.typesafe.config.Config
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

object KafkaLauncher {

  implicit val system: ActorSystem = ActorSystem.create("kafka_producer")
  val producerConfig: Config = system.settings.config.getConfig("akka.kafka.producer")
  val consumerConfig: Config = system.settings.config.getConfig("akka.kafka.consumer")
  val producerSettings: ProducerSettings[String, String] = ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)
  val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)
  val topic = "test"
  val bootstrapServers = "PLAINTEXT://pi-server-03:9092"


  def main(args: Array[String]): Unit = {
    Producers.produce(topic, producerSettings)
    Consumers.consume(topic, consumerSettings)
      .toMat(Sink.ignore)(DrainingControl.apply)
      .run()
  }
}
