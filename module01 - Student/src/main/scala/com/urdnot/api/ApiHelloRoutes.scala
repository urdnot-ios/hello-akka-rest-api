package com.urdnot.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{concat, get, path, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.IOResult
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import com.typesafe.scalalogging.Logger

import java.nio.file.Path
import scala.concurrent.{ExecutionContextExecutor, Future}

trait ApiHelloRoutes {


  final case class User(userId: String, message: String)
  private val file: Path = ???

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val log: Logger = Logger("routes")
  val fileSink: Sink[ByteString, Future[IOResult]] = ???
  val appendFlow: Flow[ByteString, ByteString, _] = ???

  def myUserPassAuthenticator(credentials: Credentials): Option[String] = ???

  def setupRoutes(): Route = {
    concat(
      path("hello") {
        get {
          parameters( ??? ) { ??? }
        }
      },
      path("helloJson") {
        post { ??? }
      },
      path("secureRoute") {
        post( ??? )
      }
    )
  }
}
