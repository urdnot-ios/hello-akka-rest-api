package com.urdnot.api

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Success, Try}


object JsonApp {
  private val config: Config = ConfigFactory.load()
  private val log: Logger = Logger("JsonApp")
  implicit val system: ActorSystem = ActorSystem("JsonHandler")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val jsonActor: ActorRef = system.actorOf(Props(new JsonHandler()))

  def main(args: Array[String]): Unit = {
    implicit val timeout: Timeout = 5.seconds
    val helloTest: Future[String] = (jsonActor ? "hello").mapTo[String]
    helloTest.onComplete {
      case Success(x) => log.info(x)
      case _ => log.info("OH NO!")
    }
  }
}
