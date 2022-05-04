package com.urdnot.api

import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import akka.stream.alpakka.udp.Datagram
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

  val serverSource: Source[Datagram, Promise[Option[Datagram]]] = ???
  val bindToLocal: InetSocketAddress = ???
  val bindFlow: Flow[Datagram, Datagram, Future[InetSocketAddress]] = ???
  val processMessage: Flow[Datagram, _, _] = ???


  def run(): Unit = {
    serverSource
      .via(bindFlow)
      .via(processMessage)
      .run()
  }
}

