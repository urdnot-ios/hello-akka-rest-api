package com.urdnot.api

import akka.actor.Props


object ApiListener {
//  case class Bid(userId: String, offer: Int)
//  case object GetBids
//  case class Bids(bids: List[Bid])

  def props(): Props = Props()
}
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
