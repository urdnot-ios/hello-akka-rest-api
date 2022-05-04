package com.urdnot.api

import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import akka.stream.alpakka.udp.Datagram
import akka.stream.alpakka.udp.scaladsl.Udp
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.typesafe.config.{Config, ConfigFactory}

import java.net.InetSocketAddress


trait SendUdp {
  val config: Config = ConfigFactory.load()
  val port: Int = config.getInt("akka.client.port")
  val server: String = config.getString("akka.client.host")
  implicit val system: ClassicActorSystemProvider = ActorSystem("udp-sender")
  val destination: InetSocketAddress = new InetSocketAddress(server, port)
  val messagesToSend = 100

  def run(): Unit = {
    Source(1 to messagesToSend)
      .map(i => ByteString(s"Message $i"))
      .map(Datagram(_, destination))
      .runWith(Udp.sendSink())
  }
}
