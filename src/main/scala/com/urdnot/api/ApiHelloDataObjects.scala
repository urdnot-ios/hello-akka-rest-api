package com.urdnot.api

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.urdnot.api.ApiHelloActor.{Bid, Bids, HelloRequest}

trait ApiHelloDataObjects extends SprayJsonSupport with DefaultJsonProtocol {
  // these are from spray-json
  implicit val bidFormat: RootJsonFormat[Bid] = jsonFormat2(Bid)
  implicit val bidsFormat: RootJsonFormat[Bids] = jsonFormat1(Bids)
  implicit val helloFormat: RootJsonFormat[HelloRequest] = jsonFormat1(HelloRequest)
}
