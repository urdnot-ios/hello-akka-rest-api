package com.urdnot.api

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import com.urdnot.api.ApiHelloApp.{executionContext, system}
import com.urdnot.api.ApiHelloRoutes.log
import com.urdnot.api.JsonHandler.{SimpleJsonReply, SimpleJsonRequest}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object JsonHandler {
  case class SimpleJsonRequest(
                                stringItem: String,
                                intItem: Int,
                                listItem: List[Int])
  case class SimpleJsonReply(
                              stringItem: String,
                              intItem: Int,
                              listItemSum: Int)
  case class routeMessage(route: String, entity: String)
  def props(): Props = Props(new JsonHandler())
}

class JsonHandler() extends Actor {
  implicit val timeout: Timeout = 5.seconds
  private val log: Logger = Logger("jsonHandler")
    def receive: Receive = {
      case x: SimpleJsonRequest =>
        sender() ! simpleRequest(x)
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
}