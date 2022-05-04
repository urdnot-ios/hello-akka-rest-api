package com.urdnot.api

import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.ServerBinding
import akka.stream.scaladsl.{Flow, Framing, Source, Tcp}
import akka.util.ByteString
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContext, Future}

trait ListenTcp {
  val config: Config = ConfigFactory.load()
  val host: String = config.getString("akka.server.host")
  val port: Int = config.getInt("akka.server.port")
  implicit val system: ActorSystem = ActorSystem("tcp-listener")
  implicit val ec: ExecutionContext = system.dispatcher

  private val log: Logger = Logger("tcp")

  val connections: Flow[Tcp.IncomingConnection, _, _] = Flow[Tcp.IncomingConnection].map{ x =>
    log.info("new connection from: " + x.remoteAddress)
//    Convenience shortcut for: flow.joinMat(handler)(Keep.right).run()
    x.handleWith(
      Flow[ByteString]
        .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
        .map(_.utf8String)
        .map{x =>
          log.info(x)
          "message processed\n"}
        .map(ByteString(_)))
  }

  def run(binding: Source[Tcp.IncomingConnection, Future[ServerBinding]]): Unit = {
    binding
      .via(connections)
      .run()
  }

}
