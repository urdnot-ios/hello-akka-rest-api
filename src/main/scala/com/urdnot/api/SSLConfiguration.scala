package com.urdnot.api

import java.io.InputStream
import java.security.{ KeyStore, SecureRandom }
import javax.net.ssl.{ KeyManagerFactory, SSLContext, TrustManagerFactory }
import akka.http.scaladsl.{ ConnectionContext, HttpsConnectionContext }


trait SSLFactory {
  implicit def sslContext: SSLContext = {

    val password: Array[Char] = "change me".toCharArray // do not store passwords in code, read them from somewhere safe!

    val ks: KeyStore = KeyStore.getInstance("PKCS12")
    val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("server.p12")

    require(keystore != null, "Keystore required!")
    ks.load(keystore, password)

    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(ks, password)

    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    tmf.init(ks)

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
    val https: HttpsConnectionContext = ConnectionContext.https(sslContext)
  }
}

//
//
//    val keystore = "keystore.jks"
//    val ksPassword: Array[Char] = context.system.settings.config.getString("security.jks-pw").toCharArray
//
//    val ks: KeyStore = KeyStore.getInstance("JKS")
//    val in = getClass.getClassLoader.getResourceAsStream(keystore)
//    require(in != null, "Bad java key storage file: " + keystore)
//    keyStore.load(in, password.toCharArray)
//
//    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
//    keyManagerFactory.init(keyStore, password.toCharArray)
//
//    val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
//    trustManagerFactory.init(keyStore)

//    val context = SSLContext.getInstance("TLS")
//    context.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, new SecureRandom)
//    context
//  }

//  implicit def sslEngineProvider: ServerSSLEngineProvider = {
//    ServerSSLEngineProvider { engine =>
//      engine.setEnabledCipherSuites(Array("TLS_RSA_WITH_AES_256_CBC_SHA"))
//      engine.setEnabledProtocols(Array("SSLv3", "TLSv1"))
//      engine
//    }
//  }
//}
//
//class ApiListener extends Actor with ActorLogging {
// Keeping for reference for later when I need to do TLS

////  def tlsSetup(): HttpsConnectionContext = {
////
////    /*
////    Steps for loading the hosts private SSL pub/priv key for the TLS handshake
////    // 1-create a keystore instance, JKS style
////    */
////    val ks: KeyStore = KeyStore.getInstance("JKS")
////
////    // best to have the password as an ENV variable, this way it doesn't end up in
////    // any repos
////
////    val ksPassword: Array[Char] = context.system.settings.config.getString("security.jks-pw").toCharArray
//////    val ksPassword: Array[Char] = new String(Files.readAllBytes(Paths.get(context.system.settings.config.getString("security.jks-pw")))).toCharArray
////
////    // Now the actual key file
////    // This is Java at it's most messed up. You have to set the path as a system property
////    // BUT you can't pull that path from an env variable. The best option is to set a string
////    // in application.conf that points to the location that will be in the Docker container
////    // Or maybe some kind of Vault system?
//////    System.setProperty("javax.net.ssl.keyStore", "/Users/jsewell/host_certs/cert/keystore.jks")
////
//////    System.setProperty("javax.net.ssl.keyStore", context.system.settings.config.getString("security.jks-file"))
//////    System.setProperty("javax.net.ssl.keyStore", System.getenv("KEYSTORE_PATH"))
//////    val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("keystore.jks")
////    val keystore = new FileInputStream(context.system.settings.config.getString("security.jks-file"))
//////    val keystore = new FileInputStream(new File("/Users/jsewell/host_certs/cert/keystore.jks"))
//////        val keystore = new FileInputStream(new File("/etc/security/keystore.jks"))
//////    val keystore = new FileInputStream(new File(System.getProperty("javax.net.ssl.keyStore")))
////
////
//////    require(keystore != null, "Must have a Keystore file!")
////
////    // ok, now add the keystore/password to the keystore instance
////    ks.load(keystore, ksPassword)
////
////    // and now the keysore manager is created
////    //val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
////    val kmf: KeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
////    kmf.init(ks, ksPassword)
////
////    // then the trust manager for inbound verification
////    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
////    tmf.init(ks)
////
////    // finally init the SSL
////
////    val sslContext: SSLContext = SSLContext.getInstance("TLS")
////    sslContext.init(kmf.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
////    ConnectionContext.https(sslContext)
////  }
//}
//
//
