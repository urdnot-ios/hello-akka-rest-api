package com.urdnot.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContextExecutor


object ApiHelloApp extends App {
  private val config: Config = ConfigFactory.load()
  private val interface: String = config.getString("akka.server.interface")
  private val httpPort: Int = config.getInt("akka.server.http.port")
  private val log: Logger = Logger("homeApiService")

  // because of the bindingFuture, you need to include the executionContext
  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // setup the routes
  val route = ApiHelloRoutes.setupRoutes()

  // bind the interface
  List(Http()
    .newServerAt(interface, httpPort)
    .bind(route)
  )
    .map { bindingFuture =>
      try {
        bindingFuture.map { serverBinding =>
          log.info(s"RestApi bound to ${serverBinding.localAddress.getAddress.getHostAddress}:${serverBinding.localAddress.getPort}")
        }
      }
      catch {
        case ex: Exception ⇒
          log.error(ex + s" Failed to bind!")
          system.terminate()
      }
    }
}
