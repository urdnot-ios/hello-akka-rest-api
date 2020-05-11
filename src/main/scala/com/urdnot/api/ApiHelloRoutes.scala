package com.urdnot.api

import com.urdnot.api.ApiHelloActor.{Bid, Bids, GetBids, HelloRequest}
import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{complete, concat, get, parameter, path, put}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Future
import akka.pattern.ask

object ApiHelloRoutes extends ApiHelloDataObjects {
  def setupRoutes(auction: ActorRef): Route = {
    path("auction") {
      concat(
        put {
          parameter("bid".as[Int], "user") { (bid, user) =>
            // place a bid, fire-and-forget
            auction ! Bid(user, bid)
            complete((StatusCodes.Accepted, "bid placed"))
          }
        },
        get {
          implicit val timeout: Timeout = 5.seconds

          // query the actor for the current auction state
          val bids: Future[Bids] = (auction ? GetBids).mapTo[Bids]
          complete(bids)
        }
      )
    }
    path("hello") {
      concat(
        get {
          implicit val timeout: Timeout = 5.seconds
          parameter("user") { userName =>
            val helloReplyMessage: Future[HelloRequest] = ((auction ? HelloRequest(userName)).mapTo[HelloRequest])
            complete(helloReplyMessage)
          }
        }
      )
    }
  }
}
