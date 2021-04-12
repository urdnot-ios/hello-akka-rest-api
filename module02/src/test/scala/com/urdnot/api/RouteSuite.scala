package com.urdnot.api

import akka.actor.Props
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class RouteSuite
  extends AnyWordSpec
    with Matchers
    with ScalatestRouteTest {

  val apiRoutes: Route = ApiHelloRoutes.setupRoutes(system.actorOf(Props(new JsonHandler())))
  val username = "jsewell"
  val message = "hello"

  "The service" should {
      "return hello username" in {
        // tests:
        Get(s"""/hello?user=${username}&message=${message}""") ~> apiRoutes ~> check {
          responseAs[String] shouldEqual """{"hello" : "jsewell"}"""
        }
      }
    }
}
