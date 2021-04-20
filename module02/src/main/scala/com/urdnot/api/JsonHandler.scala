package com.urdnot.api

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import com.urdnot.api.JsonHandler.{SimpleGreeting, SimpleJsonReply, SimpleJsonRequest, helloMessage}
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import io.circe.{HCursor, Json}

import scala.concurrent.duration.DurationInt

object JsonHandler {
  case class SimpleGreeting(message: String, name: String)
  case class SimpleJsonRequest(
                                stringItem: String,
                                intItem: Int,
                                listItem: List[Int])
  case class SimpleJsonReply(
                              stringItem: String,
                              intItem: Int,
                              listItemSum: Int)
  case class helloMessage(userName: String, message: String)
  def props(): Props = Props(new JsonHandler())
}

class JsonHandler() extends Actor {
  implicit val timeout: Timeout = 5.seconds
  private val log: Logger = Logger("jsonHandler")
    def receive: Receive = {
      case x: SimpleJsonRequest =>
        sender() ! simpleRequest(x)
      case x: helloMessage => sender() ! SimpleGreeting("hello", x.userName).asJson
      case j: Json => sender() ! jsonRequest(j)
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
}