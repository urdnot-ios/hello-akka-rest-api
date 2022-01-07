package com.urdnot.api

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import akka.stream.{Materializer, SystemMaterializer}
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContext, Future}

object Consumers {
  implicit val system: ActorSystem = ActorSystem.create("kafka_producer")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = SystemMaterializer(system).materializer
  implicit val scheduler: akka.actor.Scheduler = system.scheduler
  val log: Logger = Logger("KafkaConsumer")

  def consume(topic: String, consumerSettings: ConsumerSettings[String, String]): Source[Int, Consumer.Control] = {
   Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(10) { msg =>
        business(msg.key, msg.value)
      }
  }

  def business(key: String, value: String): Future[Int] = {
    log.info(value.mkString)
    Future.successful(value.toInt)
  }
}
