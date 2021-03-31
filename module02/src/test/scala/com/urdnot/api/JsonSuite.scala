package com.urdnot.api

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.urdnot.api.JsonApp.log
import io.circe.{Json, ParsingFailure}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class JsonSuite extends TestKit(ActorSystem("JsonSuite"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  val validJson: Array[Byte] =
    """
{
  "foo": "bar",
  "baz": 123,
  "list of stuff": [ 4, 5, 6 ]
}
""".getBytes

  val invalidJson: Array[Byte] =
    """NOPE""".getBytes

  implicit val timeout: Timeout = 5.seconds

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A valid basic JSON parser" must {
    "send back a valid JSON object" in {
      val basic = system.actorOf(JsonHandler.props)
      (basic ? validJson).mapTo[Either[ParsingFailure, Json]]
      .onComplete {
        case Success(x) => x match {
          case Left(j) => j
          case Right(e) => e
        }
        case Failure(e) => e
      }
      expectMsg(Json)
    }
  }

  "An invalid basic JSON parser" must {
    "send back a parsingfailure" in {
      val basic = system.actorOf(JsonHandler.props)
      (basic ? invalidJson).mapTo[Either[ParsingFailure, Json]]
        .onComplete {
          case Success(x) => x match {
            case Left(j) => j
            case Right(e) => e
          }
          case Failure(e) => e
        }
      expectMsg(ParsingFailure)
    }
  }
  "A valid basic JSON parser" must {
    "send back a valid JSON object" in {
      val basic = system.actorOf(JsonHandler.props)
      (basic ? validJson).mapTo[Either[ParsingFailure, Json]]
        .onComplete {
          case Success(x) => x match {
            case Left(j) => j
            case Right(e) => e
          }
          case Failure(e) => e
        }
      expectMsg(Json)
    }
  }

  "An invalid basic JSON parser" must {
    "send back a parsingfailure" in {
      val basic = system.actorOf(JsonHandler.props)
      (basic ? invalidJson).mapTo[Either[ParsingFailure, Json]]
        .onComplete {
          case Success(x) => x match {
            case Left(j) => j
            case Right(e) => e
          }
          case Failure(e) => e
        }
      expectMsg(ParsingFailure)
    }
  }
}
