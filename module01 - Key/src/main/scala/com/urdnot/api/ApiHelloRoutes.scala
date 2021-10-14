package com.urdnot.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, concat, get, path, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.Logger

import java.nio.file.{Files, Path}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

trait ApiHelloRoutes {

  final case class User(userId: String, message: String)
  private val file: Path = Files.createTempFile("test", ".tmp")

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val log: Logger = Logger("routes")
  val appendFlow: Flow[ByteString, ByteString, _] = Flow[ByteString].map { x: ByteString =>
    x.concat(ByteString("more data"))
  }
  val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(f = file)

  def authenticator(credentials: Credentials): Option[String] = {
    credentials match {
      case p @ Credentials.Provided(id) if p.verify("somepass") => Some(id)
      case _ => log.error(credentials.toString); Some("unauthorized")
    }
  }

  def setupRoutes(): Route = {
    concat(
      path("hello") {
        get {
          withSizeLimit(1024L) {
            parameters("user".as[String], "message".as[String]).as(User) { user: User =>
              val returnMessage: String = user.message match {
                case "hello" => s"Hello to you too, ${user.userId}"
                case "goodbye" => s"Goodbye, ${user.userId}, thanks for checking in!"
                case _ => "I'm sorry, I don't understand you"
              }
              complete(returnMessage)
            }
          }
        }
      },
      path("helloJson") {
        post {
          withSizeLimit(40L) {
            extractDataBytes { bytes: Source[ByteString, Any] =>
              log.info(file.toString)
              val finishedWriting: Future[IOResult] =
                bytes
                  .via(appendFlow)
                  .runWith(fileSink)
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
      },
      path("secureRoute") {
        post {
          authenticateBasic(realm = "", authenticator = authenticator) {
            case x: String if x == "user" => complete(StatusCodes.OK)
            case _ => complete(StatusCodes.Unauthorized)
          }
        }
      }
    )
  }
}
