package com.urdnot.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, concat, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.Attributes
import akka.stream.scaladsl.{FileIO, Flow}
import akka.util.ByteString
import com.typesafe.scalalogging.Logger
import com.urdnot.api.ApiHelloApp.system

import java.io.File
import java.nio.file.StandardOpenOption.{APPEND, CREATE, WRITE}
import scala.util.{Failure, Success}

object ApiHelloRoutes {

  final case class User(userId: String, message: String)

  private val log: Logger = Logger("routes")
  val appendFlow: Flow[ByteString, ByteString, _] = Flow[ByteString].map { x: ByteString =>
    x.concat(ByteString("more data"))
  }

  def setupRoutes(): Route = {
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
        post {
          withoutSizeLimit {
            extractDataBytes { bytes =>
              val finishedWriting =
                bytes
                  .log("bytes", _.utf8String)
                  .addAttributes(Attributes
                    .logLevels(
                      onElement = Attributes.LogLevels.Info,
                      onFinish = Attributes.LogLevels.Info,
                      onFailure = Attributes.LogLevels.Error)
                  ).via(appendFlow)
                  .runWith(FileIO.toPath(f = new File("/tmp/example.out").toPath, options = Set(WRITE, APPEND, CREATE))
                  )
              onComplete(finishedWriting) {
                case Success(x) => complete("Finished writing data: " + x.count + " bytes written to file")
                case Failure(e) =>
                  println(e.getStackTrace.mkString("Array(", ", ", ")"))
                  log.error(e.toString)
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        }

      }
    )
  }
}
