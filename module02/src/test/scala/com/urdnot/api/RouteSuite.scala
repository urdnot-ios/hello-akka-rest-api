package com.urdnot.api

import akka.actor.Props
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class RouteSuite
  extends AnyWordSpec
    with Matchers
    with ScalatestRouteTest
    with DataSuite {

  val apiRoutes: Route = ApiHelloRoutes.setupRoutes(system.actorOf(Props(new JsonHandler())))

  "The basic service" should {
      "return hello username" in {
        // tests:
        Get(s"""/hello?user=$username&message=$message""") ~> apiRoutes ~> check {
          responseAs[String] shouldEqual simpleHelloReply
        }
      }
    }
  "The jsonExtractor service" should {
    "return a new JSON string for valid JSON" in {
      // tests:
      Post("/jsonExtractor", content = validJsonExtractor) ~> apiRoutes ~> check {
        responseAs[String].toDouble shouldEqual validJsonExtractorResult
      }
    }
  }
  "The jsonExtractor service" should {
    "return an error for invalid JSON" in {
      // tests:
      Post("/jsonExtractor", content = badJsonExtractor) ~> apiRoutes ~> check {
        response._1 shouldEqual badJsonReply
      }
    }
  }
}
