package com.urdnot.api

import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}


object ApiHelloApp extends App with ApiHelloRoutes {
  private val config: Config = ConfigFactory.load()
  private val interface: String = config.getString("akka.server.interface")
  private val httpPort: Int = config.getInt("akka.server.http.port")
  private val httpsPort: Int = config.getInt("akka.server.https.port")
  private val log: Logger = Logger("homeApiService")

  // setup the TLS password
  val password = getClass.getClassLoader.getResourceAsStream("password").readAllBytes().map(_.toChar).mkString.trim.toCharArray

  // setup the Keystore
  val ks: KeyStore = KeyStore.getInstance("PKCS12")
  val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("example.com.jks")

  // require it before loading
  require(keystore != null, "Keystore required!")
  ks.load(keystore, password)

  val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, password)

  // now the trust store
  val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  tmf.init(ks)

  // finally the TLS
  val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
  val https: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)


  // setup the routes
  val route = setupRoutes()

  // bind the interface, load the routes, and catch any errors
  // you can run both HTTP and HTTPS in the same application as follows:
  List(
    Http().newServerAt(interface, httpsPort).enableHttps(https).bind(route),
    Http().newServerAt(interface, httpPort).bind(route)
  ).map { bindingFuture =>
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
