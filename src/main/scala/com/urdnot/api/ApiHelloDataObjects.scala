package com.urdnot.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.urdnot.api.ApiHelloActor.HelloRequest
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

// all your json structures go here, inbound and outbound
// they reference classes in your actors

trait ApiHelloDataObjects extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val helloFormat: RootJsonFormat[HelloRequest] = jsonFormat2(HelloRequest)
}
