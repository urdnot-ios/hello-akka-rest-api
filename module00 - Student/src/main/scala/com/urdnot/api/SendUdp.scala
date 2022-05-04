package com.urdnot.api

import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import com.typesafe.config.{Config, ConfigFactory}


trait SendUdp {
  val config: Config = ConfigFactory.load()
  val port: Int = config.getInt("akka.server.port")
  val server: String = config.getString("akka.client.host")
  implicit val system: ClassicActorSystemProvider = ActorSystem("udp-sender")
  val destination = ???

  def run(): Unit = {
   ???
  }
}
