package com.urdnot.api

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.{Materializer, SystemMaterializer}
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.{ExecutionContext, Future}


object Producers {

  implicit val system: ActorSystem = ActorSystem.create("kafka_producer")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = SystemMaterializer(system).materializer
  implicit val scheduler: akka.actor.Scheduler = system.scheduler

  def produce(topic: String, producerSettings: ProducerSettings[String, String]): Future[Done] = {
    Source(1 to 100)
      .map(value => new ProducerRecord[String, String](topic, value.toString))
      .runWith(Producer.plainSink(producerSettings))
  }
}
