package com.urdnot.api

import akka.actor.{Actor, ActorLogging, Props}
import com.urdnot.api.ApiHelloActor.{Bid, Bids, GetBids, HelloRequest}

object ApiHelloActor {
  case class Bid(userId: String, offer: Int)
  case object GetBids
  case class Bids(bids: List[Bid])
  case class HelloRequest(userName: String)

  def props() = Props
}
class ApiHelloActor extends Actor with ActorLogging with ApiHelloDataObjects {

  var bids = List.empty[Bid]
  def receive = {
    case bid @ Bid(userId, offer) =>
      bids = bids :+ bid
      log.info(s"Bid complete: $userId, $offer")
    case GetBids => sender() ! Bids(bids)
    case HelloRequest(userName) => sender() ! s"Hello there, ${userName}"
    case _       => log.info("Invalid message")
  }
}
