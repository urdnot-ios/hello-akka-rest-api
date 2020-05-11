package com.urdnot.api

import com.urdnot.api.ApiHelloActor.HelloRequest
import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{complete, concat, get, parameter, path}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import akka.pattern.ask

object ApiHelloRoutes extends ApiHelloDataObjects {
  // There might be a better way to do this, but I like putting all the routes
  // together in the same object. Provide examples of how to call them
  // you can do an additonal concat below a path if you want different parameters to
  // do different things but under the same path
  // In Intellij, using Akka 2.6 and Scala 2.13 I get an error in the parameter() map, however it
  // compiles and runs fine.

  def setupRoutes(auction: ActorRef): Route = {
    // curl 'http://localhost:8080/hello?user=jsewell&message=hello'
    concat(
      path("hello") {
        get {
          implicit val timeout: Timeout = 5.seconds
          parameter("user", "message") { (userName, message) =>
            val helloReplyMessage: Future[HelloRequest] = (auction ? HelloRequest(userName, message)).mapTo[HelloRequest]
            complete(helloReplyMessage)
          }
        }
      },
      // curl -d '{"userName":"jsewell","message":"hello"}' -H "Content-Type: application/json" -X POST http://localhost:8080/helloJson
      path("helloJson") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[HelloRequest]) { jsonRequest: HelloRequest =>
              val helloReplyMessage: Future[HelloRequest] = (auction ? HelloRequest(jsonRequest.userName, jsonRequest.message)).mapTo[HelloRequest]
              complete(helloReplyMessage)
            }
          }
        )
      }
    )
  }
}

