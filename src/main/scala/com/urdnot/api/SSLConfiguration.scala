package com.urdnot.api

import akka.http.scaladsl.{ConnectionContext, HttpsConnectionContext}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import java.io.FileInputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}


trait SSLConfiguration {

    private val log: Logger = Logger("SSLSetup")
    private val config: Config = ConfigFactory.load()
    val ks: KeyStore = KeyStore.getInstance("PKCS12")
    val ksPassword: Array[Char] = config.getString("security.pkcs12-pw").trim.toCharArray
    val keystore = new FileInputStream(config.getString("security.pkcs12-file"))
    require(keystore != null, "Must have a Keystore file!")

    ks.load(keystore, ksPassword)

    val kmf: KeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    kmf.init(ks, ksPassword)

    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    tmf.init(ks)

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(kmf.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
//    val https: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)

    val https: HttpsConnectionContext = ConnectionContext.httpsServer(() => {
        val engine = sslContext.createSSLEngine()
        engine.setUseClientMode(false)
        engine.setNeedClientAuth(true)
        engine
    })
    //#require-client-auth

}