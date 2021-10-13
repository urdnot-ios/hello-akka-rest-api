package com.urdnot.api

import akka.http.scaladsl.Http
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger


object ApiHelloApp extends App with ApiHelloRoutes {
  private val config: Config = ConfigFactory.load()
  private val interface: String = config.getString("akka.server.interface")
  private val httpPort: Int = config.getInt("akka.server.http.port")
  private val log: Logger = Logger("homeApiService")

  // setup the routes
  val route = setupRoutes()

  // bind the interface, load the routes, and catch any errors
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
