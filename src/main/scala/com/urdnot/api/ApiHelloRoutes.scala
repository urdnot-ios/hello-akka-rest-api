package com.urdnot.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, concat, get, parameter, path, _}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import com.urdnot.api.ApiHelloActor.{HelloMessage, HelloReplyMessage}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser._

import scala.concurrent.Future
import scala.concurrent.duration._

object ApiHelloRoutes {

  private val log: Logger = Logger("routes")
  def setupRoutes(helloActor: ActorRef): Route = {
    // curl 'http://Jeffreys-MBP-16.urdnot.com:8081/hello?user=jsewell&message=hello'
    concat(
      path("hello") {
        get {
          implicit val timeout: Timeout = 5.seconds
          parameter("user") { userName: String =>
            val helloReplyMessage: Future[HelloReplyMessage] = (helloActor ? HelloMessage(userName = userName, message = "")).mapTo[HelloReplyMessage]
            onSuccess(helloReplyMessage) {
              case helloReplyMessage: HelloReplyMessage => complete(helloReplyMessage.message)
              case _ => complete(StatusCodes.InternalServerError)
            }
//            complete(helloReplyMessage)
          }
        }
      },
      // curl -d '{"userName":"jsewell","message":"hello"}' -H "Content-Type: application/json" -X POST http://Jeffreys-MBP-16.urdnot.com:8081/helloJson
      path("helloJson") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            // decode[Foo](json)
            entity(as[String]) { jsonRequest: String =>
              implicit val fooDecoder: Decoder[HelloMessage] = deriveDecoder[HelloMessage]
              val parsedMessage: HelloMessage = decode[HelloMessage](jsonRequest) match {
                case Left(failure) => HelloMessage("", "")
                case Right(json) => json.asInstanceOf[HelloMessage]
              }
              val helloReplyMessage: Future[HelloReplyMessage] = (helloActor ? parsedMessage).mapTo[HelloReplyMessage]
              onSuccess(helloReplyMessage) {
                case helloReplyMessage: HelloReplyMessage => complete(helloReplyMessage.message)
                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          }
        )
      }
    )
  }
}
// io.circe.parser.parse(jsonRequest).asInstanceOf[HelloMessage]
