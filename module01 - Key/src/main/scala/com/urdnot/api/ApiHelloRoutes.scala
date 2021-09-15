package com.urdnot.api

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{complete, concat, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import com.typesafe.scalalogging.Logger
import com.urdnot.api.ApiHelloApp.system

import java.io.File
import java.nio.file.StandardOpenOption.{APPEND, CREATE, WRITE}
import scala.util.{Failure, Success}

object ApiHelloRoutes {

  final case class User(userId: String, message: String)
  private val log: Logger = Logger("routes")
  def setupRoutes(helloActor: ActorRef): Route = {
    // curl 'http://Jeffreys-MBP-16.urdnot.com:8081/hello?user=jsewell&message=hello'
    // curl 'http://Jeffreys-MBP-16.urdnot.com:8081/hello?user=jsewell&message=goodbye'
    // curl 'http://Jeffreys-MBP-16.urdnot.com:8081/hello?user=jsewell&message=GARBAGE'
    concat(
      path("hello") {
        get {
          parameters("user".as[String], "message".as[String]).as(User) { user: User =>
            val returnMessage: String = user.message match {
              case "hello" => s"Hello to you too, ${user.userId}"
              case "goodbye" => s"Goodbye, ${user.userId}, thanks for checking in!"
              case _ => "I'm sorry, I don't understand you"
            }
              complete(returnMessage)
          }
        }
      },
      // curl -d '{"userName":"jsewell","message":"hello"}' -H "Content-Type: application/json" -X POST http://Jeffreys-MBP-16.urdnot.com:8081/helloJson
      path("helloJson") {
        concat(
          post {
            withoutSizeLimit {
              extractDataBytes { bytes =>
                val finishedWriting = bytes
                  .runWith(
                    FileIO
                      .toPath(f = new File("/tmp/example.out").toPath, options = Set(WRITE, APPEND, CREATE))
                  )
                onComplete(finishedWriting) {
                  case Success(x) => complete("Finished writing data: " + x.count + " bytes written to file")
                  case Failure(exception) => complete("Something went wrong: " + exception.getMessage)
                }
              }
            }
          }
        )
      }
    )
  }
}
