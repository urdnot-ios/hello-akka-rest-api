package com.urdnot.iot

import akka.actor.ActorSystem
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

class ListenUdpSuite extends TestKit(ActorSystem("UdpSpec"))
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
    " receive messages" in {
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
  }
}