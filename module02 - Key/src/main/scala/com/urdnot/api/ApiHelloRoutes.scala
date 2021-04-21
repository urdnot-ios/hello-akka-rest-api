package com.urdnot.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, concat, get, onSuccess, parameter, path, _}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import com.urdnot.api.JsonHandler.{HelloMessage, SimpleJsonReply, SimpleJsonRequest, WeatherMeasurement}
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser._
import io.circe.{Decoder, Json}

import scala.concurrent.Future
import scala.concurrent.duration._

object ApiHelloRoutes {

  private val log: Logger = Logger("routes")
  def setupRoutes(jsonHandler: ActorRef): Route = {
    concat(
      path("hello") {
        get {
          implicit val timeout: Timeout = 5.seconds
          parameter("user", "message") { (userName: String, message: String) =>
            log.info(HelloMessage(message = "hello", userName = userName).toString)
            val helloReplyMessage = jsonHandler ? HelloMessage(message = message, userName = userName)
            onSuccess(helloReplyMessage) {
              case s: Json => complete(s.toString())
              case _ => complete(StatusCodes.InternalServerError)
            }
          }
        }
      },
      path("simpleJson") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[String]) { jsonRequest: String =>
              implicit val simpleJsonDecoder: Decoder[SimpleJsonRequest] = deriveDecoder[SimpleJsonRequest]
              decode[SimpleJsonRequest](jsonRequest) match {
                case Left(x) => log.error("bad request: " + x.getMessage)
                  complete(StatusCodes.BadRequest)
                case Right(parsedMessage: SimpleJsonRequest) => onSuccess(
                  (jsonHandler ? parsedMessage).mapTo[SimpleJsonReply]) {
                  case simpleJsonReply: SimpleJsonReply => complete(simpleJsonReply.listItemSum.toString)
                  case _ => log.error("unable to process reply")
                    complete(StatusCodes.InternalServerError)
                }
              }
            }
          }
        )
      },
      path("jsonExtractor") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[String]) { jsonRequest: String =>
              val parsedVal = (jsonHandler ? parse(jsonRequest).getOrElse(0)).mapTo[String]
               parsedVal match {
                case x: Future[String] => complete(x)
                case _ => log.error("unable to process reply")
                  complete(StatusCodes.InternalServerError)
                }
            }
          }
        )
      },
      path("weather") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[String]) { jsonRequest: String =>
              val parsedVal: Future[String] = (jsonHandler ? ("weatherMessage", jsonRequest)).mapTo[String]
              parsedVal match {
                case x: Future[String] => complete(x)
                case _ => log.error("unable to process reply")
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        )
      }
    )
  }
}
