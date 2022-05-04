package com.urdnot.iot

import akka.actor.ActorSystem
import akka.io.UdpSO
import akka.stream.Materializer
import akka.stream.alpakka.udp.Datagram
import akka.stream.alpakka.udp.scaladsl.Udp
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import akka.util.ByteString
import com.urdnot.api.ListenUdpApp.{bindFlow, processMessage}
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

  implicit val mat: Materializer = Materializer(system)
  implicit val pat: PatienceConfig = PatienceConfig(3.seconds, 50.millis)


  private def msg(msg: String, destination: InetSocketAddress) =
    Datagram(ByteString(msg), destination)

  override def afterAll =
    TestKit.shutdownActorSystem(system)

  "UDP stream" must {
    "send and receive messages" in {
      val ((pub, bound), sub) = TestSource
        .probe[Datagram](system)
        .viaMat(bindFlow)(Keep.both)
        .via(processMessage)
        .toMat(TestSink.probe)(Keep.both)
        .run()

      val destination = bound.futureValue

      {val destination = new InetSocketAddress("my.server", 27015)
        destination
      }

      // #send-datagrams
      val messagesToSend = 100

      // #send-datagrams

      sub.ensureSubscription()
      sub.request(messagesToSend)

      Source(1 to messagesToSend)
        .map(i => ByteString(s"Message $i"))
        .map(Datagram(_, destination))
        .runWith(Udp.sendSink())

      (1 to messagesToSend).foreach { _ =>
        sub.requestNext()
      }
      sub.cancel()
    }

    "send messages with options" in {

      val ((pub, bound), sub) = TestSource
        .probe[Datagram](system)
        .viaMat(bindFlow)(Keep.both)
        .via(processMessage)
        .toMat(TestSink.probe)(Keep.both)
        .run()

      val destination = bound.futureValue

      {
        val destination = new InetSocketAddress("my.server", 27015)
        destination
      }

      val messagesToSend = 100

      sub.ensureSubscription()
      sub.request(messagesToSend)

      Source(1 to messagesToSend)
        .map(i => ByteString(s"Message $i"))
        .map(Datagram(_, destination))
        .runWith(Udp.sendSink(List(UdpSO.broadcast(true))))

      (1 to messagesToSend).foreach { _ =>
        sub.requestNext()
      }
      sub.cancel()
    }

//    "ping-pong messages" in {
//      val ((pub1, bound1), sub1) = TestSource
//        .probe[Datagram](system)
//        .viaMat(Udp.bindFlow(bindToLocal))(Keep.both)
//        .via(processMessage)
//        .toMat(TestSink.probe)(Keep.both)
//        .run()
//
//      val ((pub2, bound2), sub2) = TestSource
//        .probe[Datagram](system)
//        .viaMat(Udp.bindFlow(bindToLocal))(Keep.both)
//        .via(processMessage)
//        .toMat(TestSink.probe)(Keep.both)
//        .run()
//
//      val boundAddress1 = bound1.futureValue
//      val boundAddress2 = bound2.futureValue
//
//      sub1.ensureSubscription()
//      sub2.ensureSubscription()
//
//      sub2.request(1)
//      pub1.sendNext(msg("Hi!", boundAddress2))
//      sub2.requestNext().data.utf8String shouldBe "Hi!"
//
//      sub1.request(1)
//      pub2.sendNext(msg("Hello!", boundAddress1))
//      sub1.requestNext().data.utf8String shouldBe "Hello!"
//
//      sub2.request(1)
//      pub1.sendNext(msg("See ya.", boundAddress2))
//      sub2.requestNext().data.utf8String shouldBe "See ya."
//
//      sub1.request(1)
//      pub2.sendNext(msg("Bye!", boundAddress1))
//      sub1.requestNext().data.utf8String shouldBe "Bye!"
//
//      sub1.cancel()
//      sub2.cancel()
//    }
  }
}