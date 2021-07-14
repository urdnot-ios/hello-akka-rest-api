package com.urdnot.api

import akka.kafka.testkit.scaladsl.ScalatestKafkaSpec
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

// Creates docker containers with Kafka/Zookeeper
// uses the testkit.testcontainers settings in application.conf
abstract class SpecBase(kafkaPort: Int)
  extends ScalatestKafkaSpec(kafkaPort)
    with AnyWordSpecLike
    with Matchers
    with ScalaFutures
    with Eventually {
  protected def this() = this(kafkaPort = -1)
}

