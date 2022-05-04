package com.urdnot.iot

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.udp.Datagram
import akka.stream.alpakka.udp.scaladsl.Udp
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import akka.util.ByteString
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.net.InetSocketAddress
import scala.concurrent.duration._


class SendUdpSuite extends TestKit(ActorSystem("UdpSpec"))
  with AnyWordSpecLike
  with Matchers
  with ScalaFutures
  with BeforeAndAfterAll{

  implicit val mat = Materializer(system)
  implicit val pat = PatienceConfig(3.seconds, 50.millis)

  // #bind-address
  val bindToLocal = new InetSocketAddress("localhost", 0)
  // #bind-address

  private def msg(msg: String, destination: InetSocketAddress) =
    Datagram(ByteString(msg), destination)

  override def afterAll =
    TestKit.shutdownActorSystem(system)

    "ping-pong messages" in {
      val ((pub1, bound1), sub1) = TestSource
        .probe[Datagram](system)
        .viaMat(Udp.bindFlow(bindToLocal))(Keep.both)
        .toMat(TestSink.probe)(Keep.both)
        .run()

      val ((pub2, bound2), sub2) = TestSource
        .probe[Datagram](system)
        .viaMat(Udp.bindFlow(bindToLocal))(Keep.both)
        .toMat(TestSink.probe)(Keep.both)
        .run()

      val boundAddress1 = bound1.futureValue
      val boundAddress2 = bound2.futureValue

      sub1.ensureSubscription()
      sub2.ensureSubscription()

      sub2.request(1)
      pub1.sendNext(msg("Hi!", boundAddress2))
      sub2.requestNext().data.utf8String shouldBe "Hi!"

      sub1.request(1)
      pub2.sendNext(msg("Hello!", boundAddress1))
      sub1.requestNext().data.utf8String shouldBe "Hello!"

      sub2.request(1)
      pub1.sendNext(msg("See ya.", boundAddress2))
      sub2.requestNext().data.utf8String shouldBe "See ya."

      sub1.request(1)
      pub2.sendNext(msg("Bye!", boundAddress1))
      sub1.requestNext().data.utf8String shouldBe "Bye!"

      sub1.cancel()
      sub2.cancel()
  }
}