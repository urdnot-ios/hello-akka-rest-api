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
  case class HelloReplyMessage(message: String, name: String)
  case class HelloMessage(userName: String, message: String)
  case class SimpleJsonRequest(
                                stringItem: String,
                                intItem: Int,
                                listItem: List[Int])
  case class SimpleJsonReply(
                              stringItem: String,
                              intItem: Int,
                              listItemSum: Int)
  case class WeatherMeasurement(
                          time: Option[Long],
                          model: Option[String],
                          avg_windspeed: Option[Int],
                          wind_gust: Option[Int],
                          wind_dir: Option[Int],
                          total_rain: Option[Int],
                          temp: Option[Double],
                          humidity: Option[Int],
                          lux: Option[Int],
                          uv_index: Option[Double]
                        )

  def props(): Props = Props(new JsonHandler())
}

class JsonHandler() extends Actor {
  implicit val timeout: Timeout = 5.seconds
  private val log: Logger = Logger("jsonHandler")
    def receive: Receive = {
      case x: HelloMessage => sender() ! HelloReplyMessage("hello", x.userName).asJson
      case x: SimpleJsonRequest => sender() ! simpleRequest(x)
      case j: Json => sender() ! jsonRequest(j)
      case ("weatherMessage", x: String) => sender() ! parseWeatherMessage(x)
      case _ => log.error("Unknown request, sending empty reply");
        sender() ! SimpleJsonReply("", 0, 0)
    }
  def simpleRequest(entity: SimpleJsonRequest): SimpleJsonReply = {
    log.info("simple request")
    SimpleJsonReply(
      stringItem = entity.stringItem,
      intItem = entity.intItem,
      listItemSum = entity.listItem.sum
    )
  }
  def jsonRequest(json: Json): String = {
    log.info("jsonRequest: " + json)
    val cursor: HCursor = json.hcursor
    cursor.downField("values").downField("baz").as[Double] match {
      case Right(x) => x.toString
      case Left(value) => log.error(value.message + " -- " + json); "None"
    }
  }
  def parseWeatherMessage(record: String): String = {
    import io.circe.parser._
    val genericParse: Either[ParsingFailure, Json] = parse(record)
    import io.circe.optics.JsonPath._
    genericParse match {
      case Right(x) => x match {
        case x: Json => try {
          val retVal = WeatherMeasurement(
            time = convertTimestamp(root.time.string.getOption(x)),
            model = root.model.string.getOption(x),
            avg_windspeed = root.avg_windspeed.int.getOption(x),
            wind_gust = root.wind_gust.int.getOption(x),
            wind_dir = root.wind_dir.int.getOption(x),
            total_rain = root.total_rain.int.getOption(x),
            temp = root.temp.double.getOption(x),
            humidity = root.humidity.int.getOption(x),
            lux = root.lux.int.getOption(x),
            uv_index = root.uv_index.double.getOption(x)
          )
          log.info(retVal.toString)
          retVal.asJson.toString
        } catch {
          case e: Exception => log.error("Unable to extract JSON: " + e.getMessage)
            ""
        }
        case _ => log.error("I dunno what this is, but it's not a weather message: " + x)
          ""
      }
      case Left(x) => log.error(x.getMessage)
        ""
    }
  }
  def convertTimestamp(time: Option[String]): Option[Long] = {
    time match {
      case Some(x: String) => Some(LocalDateTime.parse(x, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .toEpochSecond(ZoneOffset.ofHours(-8)))
      case None => None
    }
  }
}