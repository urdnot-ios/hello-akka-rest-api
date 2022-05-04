package com.urdnot.api

import akka.stream.scaladsl.Tcp.ServerBinding
import akka.stream.scaladsl.{Source, Tcp}

import scala.concurrent.Future

object ListenTcpApp extends ListenTcp {
  val binding: Source[Tcp.IncomingConnection, Future[ServerBinding]] =
    Tcp(system).bind(host, port)

  def main(args: Array[String]): Unit = {
    run(binding)
  }
}
