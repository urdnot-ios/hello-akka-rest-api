package com.urdnot.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, concat, get, parameter, path}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Future
import akka.pattern.ask
import com.typesafe.scalalogging.Logger
import com.urdnot.api.ApiHelloActor.{HelloMessage, HelloReplyMessage}

object ApiHelloRoutes {

  private val log: Logger = Logger("routes")
  def setupRoutes(helloActor: ActorRef): Route = {
    // curl 'https://localhost/hello?user=jsewell&message=hello'
    concat(
      path("hello") {
        get {
          implicit val timeout: Timeout = 5.seconds
          parameter("user") { userName =>
            val helloReplyMessage: Future[HelloReplyMessage] = (helloActor ? HelloMessage(userName = userName, message = None)).mapTo[HelloReplyMessage]
            onSuccess(helloReplyMessage) {
              case helloReplyMessage: HelloReplyMessage => complete(helloReplyMessage.message)
              case _ => complete(StatusCodes.InternalServerError)
            }
//            complete(helloReplyMessage)
          }
        }
      },
      // curl -d '{"userName":"jsewell","message":"hello"}' -H "Content-Type: application/json" -X POST http://localhost:443/helloJson
      path("helloJson") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[String]) { jsonRequest: String =>
              val helloReplyMessage: Future[String] = (helloActor ? jsonRequest).mapTo[String]
              complete(helloReplyMessage)
            }
          }
        )
      }
    )
  }
}

