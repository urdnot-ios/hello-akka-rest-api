package com.urdnot.api


import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.urdnot.api.ApiHelloRoutes.setupRoutes
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ApiSuite extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val routesUnderTest = setupRoutes
  "The hello route" should {
    "return a greeting for hello GET requests to the hello path" in {
      Get("/hello?user=jsewell&message=hello") ~> Route.seal(routesUnderTest) ~> check {
        responseAs[String] shouldEqual "Hello to you too, jsewell"
      }
    }
  }
  // curl 'http://Jeffreys-MBP-16.urdnot.com:8081/hello?user=jsewell&message=GARBAGE'
  "The hello route" should {
    "return a farewell for goodbye GET requests to the hello path" in {
      Get("/hello?user=jsewell&message=goodbye") ~> Route.seal(routesUnderTest) ~> check {
        responseAs[String] shouldEqual "Goodbye, jsewell, thanks for checking in!"
      }
    }
  }
  "The hello route" should {
    "return a greeting for GET requests to the hello path" in {
      Get("/hello?user=jsewell&message=GARBAGE") ~> Route.seal(routesUnderTest) ~> check {
        responseAs[String] shouldEqual "I'm sorry, I don't understand you"
      }
    }
  }
  // curl -d '{"userName":"jsewell","message":"hello"}' -H "Content-Type: application/json" -X POST http://Jeffreys-MBP-16.urdnot.com:8081/helloJson
  "The helloJson route" should {
    "Return a message and log to a file" in {
      Post("/helloJson") .withEntity(ContentTypes.`application/json`, ByteString("""{"userName":"jsewell","message":"hello"}""")) ~> Route.seal(routesUnderTest) ~> check {
        status shouldBe StatusCodes.InternalServerError
      }
    }
  }
//  addCredentials(OAuth2BearerToken(VALID_TOKEN))
}
