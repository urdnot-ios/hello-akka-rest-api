package com.urdnot.api

import akka.http.scaladsl.server.Directives.{concat, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.typesafe.scalalogging.Logger

object ApiHelloRoutes {

  final case class User(userId: String, message: String)

  private val log: Logger = Logger("routes")
  val appendFlow: Flow[ByteString, ByteString, _] = ???

  def setupRoutes(): Route = {
    concat(
      path("hello") {
        get {
          parameters(???) { ??? }
        }
      },
      path("helloJson") {
        post {???}
      }
    )
  }
}
