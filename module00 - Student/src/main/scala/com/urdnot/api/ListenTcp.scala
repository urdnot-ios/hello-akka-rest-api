package com.urdnot.api

import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.ServerBinding
import akka.stream.scaladsl.{Flow, Source, Tcp}
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

  val connections: Flow[Tcp.IncomingConnection, _, _] = ???

  def run(binding: Source[Tcp.IncomingConnection, Future[ServerBinding]]): Unit = {
    binding
      .via(connections)
      .run()
  }

}
