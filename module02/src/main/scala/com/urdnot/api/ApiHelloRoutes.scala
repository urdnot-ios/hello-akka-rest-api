package com.urdnot.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, concat, get, onSuccess, parameter, path, _}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import com.urdnot.api.JsonHandler.{SimpleJsonReply, SimpleJsonRequest, helloMessage}
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser._
import io.circe.{Decoder, Json}

import scala.concurrent.Future
import scala.concurrent.duration._

object ApiHelloRoutes extends ApiHelloDataObjects {

  private val log: Logger = Logger("routes")
  def setupRoutes(jsonHandler: ActorRef): Route = {
    // curl 'http://localhost:8081/hello?user=jsewell&message=hello'
    /*
    Take in a simple get with parameters: user and message, return a JSON formatted string saying "hello" to the user
     */
    concat(
      path("hello") {
        get {
          implicit val timeout: Timeout = 5.seconds
          parameter("user", "message") { (userName: String, message: String) =>
            log.info(helloMessage(message = "hello", userName = userName).toString)
            val helloReplyMessage = jsonHandler ? helloMessage(message = message, userName = userName)
            onSuccess(helloReplyMessage) {
              case s: Json => complete(s.toString())
              case _ => complete(StatusCodes.InternalServerError)
            }
          }
        }
      },
      // curl -d '{"stringItem":"my stuff","intItem": 3, "listItem" : [1,2,3]}' -H "Content-Type: application/json" -X POST http://localhost:8081/simpleJson
/*       expect a SimpleJsonRequest, if the inbound data can be parsed:
             1-into valid JSON
             2-into the target datatype
         then pass it on. If not, send back appropriate messages

 */

      path("simpleJson") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[String]) { jsonRequest: String =>
              implicit val simpleJsonDecoder: Decoder[SimpleJsonRequest] = deriveDecoder[SimpleJsonRequest]
              decode[SimpleJsonRequest](jsonRequest) match {
                case Left(x) => log.error("bad request: " + x.getMessage)
                  complete(StatusCodes.BadRequest)
                case Right(parsedMessage: SimpleJsonRequest) => onSuccess(
                  (jsonHandler ? parsedMessage).mapTo[SimpleJsonReply]) {
                  case simpleJsonReply: SimpleJsonReply => complete(simpleJsonReply.toString)
                  case _ => log.error("unable to process reply")
                    complete(StatusCodes.InternalServerError)
                }
              }
            }
          }
        )
      },
      //      curl -d '{"id": "c730433b-082c-4984-9d66-855c243266f0","name": "Foo", "counts": [1, 2, 3], "values": {"bar": true, "baz": 100.001, "qux": ["a", "b"]}' -H "Content-Type: application/json" -X POST http://localhost:8081/jsonExtractor
      //      curl -d '{"id": "c730433b-082c-4984-9d66-855c243266f0","name": "Foo", "counts": [1, 2, 3], "values": {"bar": true, "baz": 100.001, "qux": ["a", "b"]}}' -H "Content-Type: application/json" -X POST http://localhost:8081/jsonExtractor
      path("jsonExtractor") {
        concat(
          post {
            implicit val timeout: Timeout = 5.seconds
            entity(as[String]) { jsonRequest: String =>
              val parsedVal = (jsonHandler ? parse(jsonRequest).getOrElse(0)).mapTo[String]
               parsedVal match {
                case x: Future[String] => complete(x)
                case _ => log.error("unable to process reply")
                  complete(StatusCodes.InternalServerError)
                }
            }
          }
        )
      }
    )
  }
}
