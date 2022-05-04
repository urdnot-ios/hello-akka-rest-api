package com.urdnot.api

import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import akka.stream.alpakka.udp.Datagram
import akka.stream.alpakka.udp.scaladsl.Udp
import akka.stream.scaladsl.{Flow, Source}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import java.net.InetSocketAddress
import scala.concurrent.{Future, Promise}


trait ListenUdp {
  val config: Config = ConfigFactory.load()
  val port: Int = config.getInt("akka.server.port")
  implicit val system: ClassicActorSystemProvider = ActorSystem("udp-listener")
  private val log: Logger = Logger("udp")

  val serverSource: Source[Datagram, Promise[Option[Datagram]]] = Source.maybe
  val bindToLocal: InetSocketAddress = new InetSocketAddress("localhost", port)
  val bindFlow: Flow[Datagram, Datagram, Future[InetSocketAddress]] =
    Udp.bindFlow(bindToLocal)
  val processMessage: Flow[Datagram, _, _] = Flow[Datagram].map{message =>
    log.info("source:  " + message.remote.getHostName + ":" + message.remote.getPort)
    log.info("message: " + message.data.utf8String)
  }

  def run(): Unit = {
    serverSource
      .via(bindFlow)
      .via(processMessage)
      .run()
  }
}

