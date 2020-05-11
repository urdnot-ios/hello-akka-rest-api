package com.urdnot.api

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object ApiHelloApp extends App {
  // because of the bindingFuture, you need to include the executionContext
  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // setup the actor that does the listening and spawns the children
  val helloApi: ActorRef = system.actorOf(Props[ApiHelloActor], "helloApi")

  // setup the routes
  val route = ApiHelloRoutes.setupRoutes(helloApi)

  // bind the interface
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
