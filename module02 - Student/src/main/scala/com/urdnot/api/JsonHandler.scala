package com.urdnot.api

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import com.urdnot.api.JsonHandler.{HelloMessage, HelloReplyMessage, SimpleJsonReply, SimpleJsonRequest, WeatherMeasurement}
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import io.circe.{HCursor, Json, ParsingFailure}

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt

object JsonHandler {
  def props(): Props = Props(new JsonHandler())
}

class JsonHandler() extends Actor {

  private val log: Logger = Logger("jsonHandler")
    def receive: Receive = {
      ???
    }

}