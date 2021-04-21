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
          ???
        }
      },
      path("simpleJson") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[String]) { jsonRequest: String =>
              ???
            }
          }
        )
      },
      path("jsonExtractor") {
        concat(
          post {
            ???
          }
        )
      },
      path("weather") {
        concat(
          post {
            ???
          }
        )
      }
    )
  }
}
