#INTRODUCTION
Welcome to the first module! The goal of this module is to acquaint you with the Akka HTTP REST API and Akka Streams as the processing engine for the inbound data.

1. First, we'll setup a basic REST API and use it to accept GET requests and walkthrough basic message handling and parsing.
2. Next, we'll use POST to trigger an Akka Stream to process the message
3. Finally, we'll review authentication, SSL, and throttling for REST connections

#Overview
A very popular entrypoint for data is the REST api. Simple data is passed as paremeters and more complex data is converted into binary and sent with an indicator of what type of data it is. In our example, we will use JSON.

Ignoring, for now, the processing of the data, the simple path is:

`HTTP message --> REST API --> Route --> data extraction --> reply`

To setup these stages you will need to have `"com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,` in your build.sbt.

The server side listener is setup in 4 steps:
1. TODO: WHAT IS TCP/IP and SOCKETS
2. TODO: EXPLAIN BODY AND HEADER
3. Take a TCP socket on the host operating system and listen for connections
4. Accept the connection
5. Parse the route of the connection and pass the HTTP entity to that route for processing
6. When the route is finished processing, it calls `complete()` and inside provides the correct reply message

So let's look at each step. 

##Preparation
explain applicaiton.conf and HOCON and logback.xml

First, make sure you create a `module01-->src-->main-->resources` folder. Inside that folder, create two files:
`application.conf` and `logback.xml`

The first file will hold all of your code's setup variables. The format is outlined here. All of the Akka setup variables are stored in an "akka" section:
```akka {
  loglevel = DEBUG
  stdout-loglevel = DEBUG
  server {
    interface = "0.0.0.0"
    http {
       port = "8081"
       port = ${?HTTP_PORT}
    }
    https{
      port = "443"
      port = ${?HTTPS_PORT}
    }
  }
}
```
Notice the `loglevel` and `server` settings. For now, debug is appropriate but will be too much information later. The host IP and port are set next. By using interface `0.0.0.0` you are able to access whatever IP address is available. If there is more than one it will listen on all of them. Finally, set up the ports for both http and https. This can be any free port.
ELABORATE A BIT MORE
The syntax `${?HTTP_PORT}` is used to obtain the variable data from either an environment variable or a variable set inside the file. This allows for local testing but then is transparent when the code is deployed because the file variables will be overwritten by the production environment variables. 

With `build.sbt`, `application.conf`, and `logback.xml` setup, it's time to write some code!
ELABORATE ON build.sbt
The first two steps in setting up an Akka HTTP REST api are to build the routes and then set up the listener.

With this information, you can build a URL that will listen for connections. For example, it might look like this:

`http://localhost:8181`

However, there is still one more step to route the connection to the appropriate processing code.

##HTTP Content
The underlying data for these exercises is HTTP which has 5 components:
1. method
2. uri
3. headers
4. entity
5. protocol

###Methods
Each HTTP message must include a method. These explain how the data is going to be presented. Akka HTTP includes 7 Directives for these methods:
1. post
2. get
3. put
4. patch
5. delete
6. head
7. options

Every URI must include a method. If one is not included, then `get` is assumed. 

We will discuss `POST` and `GET` in this module.

####URIs
URI's are the address of the request. The specificaiton for them is:
`scheme://host:port/path?query`
For our uses, the "scheme" will be either `HTTP` or `HTTPS` but there are many others.
EXPLAIN MORE ABOUT URI, like "flie" and "JDBC"
The `host:port` are the service's IP address and port number. For our exercises they will be `localhost` and `8081`

The `path` is called a `route` on the server side code. The server must have routes setup so that messages can be delivered correctly. The different types of routes accept data in different, but overlapping, ways. 

Data for the path, or route, is identified using a [query string](https://en.wikipedia.org/wiki/Query_string) and will look something like this:

`http://localhost:8181?myRoute`

Notice the `?` that begins the query string

The two most common ways to send data to a route are either as "Parameters" or as "Data". 

In Akka HTTP there are a suite of built-in Directives that help to parse and process the data. We will only cover a few in this exercise, the full documentation is [here](https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/)
#####Parameters
Paremeters are sent in the URL itself using key/value pairs. They will look something like this:
`http://localhost:8181/myRoute?key1=val1&key2=val2` 

Notice the use of the ampersand `&` to distinguish sets of keys and values and the equal sign `=` to connect a key with a value. The `&` is not mandatory on all sites, but it is the default for Akka HTTP.

Any text must be [percent encoded](https://en.wikipedia.org/wiki/Percent-encoding) so that things like whitespace or apostrophes will be represented using `%20` format (percent sign followed by 2 digits).

The query parameters are quick and easy but not very flexible for more complex data. While there is no technical limit on the amount of data you can send in a query string, most browsers and servers have a built-in limit.

For Akka HTTP servers, limit is first (and optionally) set in the `application.conf` file as `akka.http.parsing.max-content-length`. A second location to set the limit is the `withSizeLimit` directive that processes the message.
#####Data
When data is sent it is base64 encoded and must be decoded by the server. This allows more complex data structures (like JSON or XML). In cURL this is denoted with `-d` and the data follows in quotes. Data is ready by Akka HTTP as a `ByteString` and must be decoded accordingly.

#####Type Safety
It is important to start routes with Type Safety. Parameters are "percent encoded" strings and data is a `ByteString`. Both should be converted to known Classes on entry. See examples [here](https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/index.html).
Always remember to try/catch the inbound data so that if it does not convert into a type Class that you will use you can return the appropriate message.

See [w3.org rfc2616](https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html) for more details

####Headers
Headers are the control plane fields of HTTP communication. Normally they are transparent to users but are heavily used by the client and server code. The full list and overview are [here](https://www.iana.org/assignments/message-headers/message-headers.xml#perm-headers%7CMessage) with a more readable Wiki article [here](https://en.wikipedia.org/wiki/List_of_HTTP_header_fields).
They are used to set-up agreements on the language, encoding, method, and authentication for the message. Normally there is no need to modify them, however when we do the authentication section they will be used to send the password.

####Entity
This is the body of the message. It will be extracted by Akka Streams and parsed according to the rules you make.

####Protocol
This is simply an indication of which version of HTTP is being used (1.0, 1.1, 2.0)

#Assignment 1: Setup and `get()`
- Create your first route inside a trait called `ApiHelloRoutes` Include inside it:
  - add your private `logger` handle
  - create the `user` case class containing a `user` string and a `message` string
  - create a function called `setupRoutes` that will take in nothing and return `akka.http.scaladsl.server.Route` type
  - inside the function use the `scalaDsl` `concatenate` helper function to build your routes
    - the first route is a `get` route that takes in 2 parameters:
      - `user` and `message` 
      - Both of these are `String` types and together can be used to build a `User` class
    - process the `User` in an anonymous function `{ user: User => ... }` that returns one of three replies:
      1. If the message is "hello" then reply with "Hello to you too, " + the username of the User
      2. If the message is "Goodbye" then reply with "Goodbye, <USER>, thanks for checking in!"
      3. If any other message comes in, reply with "I'm sorry, I don't understand you"
    - Complete the `get()` request with the appropriate message
- Now that the first route is ready, create the entrypoint for the application. Make it an object called `ApiHelloApp` and instead of using a "main()" function, simply extend the `App` object.
- Inside this object, load the config settings from the application.conf file
- Instantiate the Implicit `val`'s for the `Actor` and include an `executionContext`
- Finally, create the http server and include the `Routes` that you set up in the `ApiHelloRoutes` object 

##Questions:
- Why is the routes setup inside a trait?
- (hint: where are your ActorSystem and ExecutionContext going to be?)
- 
#Post and Binary Data
The `POST` method provides a powerful way to send more complex data to REST API services. The data payload must be base64 encoded and a `type` flag added to indicate what kind of data is being passed.

The types are either `application` and `text` with additional details below each.
The types are accessed in AkkaHTTP using the following patterns:
```
ContentTypes.`text/plain(UTF-8)`
ContentTypes.`application/json`
```
The `text` types are:
- plain
- html
- text
- csv

These are used for simple string communication

The `application` types are:
- json
- octet-stream
- xml
- x-www-form-urlencoded
- grpc+proto

These are more complex and are used to pass better structured data. 
The [reqbin](https://reqbin.com/req/c-dwjszac0/curl-post-json-example) tool has a good example of how to use cURL to POST JSON, forms, and XML


##Using Akka Streams to Manage the Data
Once you have the method ready you need to process the data. The first recommended step is to use the helper directive `withSizeLimit`. This prevents malicious or erroneous payloads from overwhelming your API. The size limit is a Long and is the number of bytes before cutoff. The pattern for routes with parameters is:
```
      path("") {
        get/put/post {
          withSizeLimit(1024L) {
            parameters().as([T]) { x: T =>
              <do something>
              ...
              complete(<return message>)
            }
          }
        }
      }
```
For routes with data you have a new option: `extractDataBytes`. This is an Akka Streams "Source" and converts the payload into a stream. This is our first chance to use Akka Streams! 
The source needs to be processed by a "Flow" and then run with a "Sink". Flows take in one data type, do something with it, and return another data type (it can be the same type).
Sinks are used to materialize the flow and the response to the client will wait for the Sink to complete.

Flows and Sinks should be built outside the route both to make the code more readable and also to allow tests to run on them. If there is an error processing the data, that must trigger a different response to the client.

The pattern for sources, flows, and sinks is:
`Source.via(flow).runWith(Sink)`
The result should be handled with a match for "Success" and "Failure"

The pattern for a post with an Akka Stream is:
```
      path("") {
        post {
          withSizeLimit(1024L) {
            extractDataBytes { bytes: Source[ByteString, Any] =>
              val finishedWriting: Future[IOResult] =
                bytes
                  .via(Flow)
                  .runWith(Sink)
              onComplete(finishedWriting) {
                case Success(x) => complete("<message>")
                case Failure(e) =>
                  println(e.getStackTrace.mkString("Array(", ", ", ")"))
                  log.error(e.toString)
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      }
```
Notice the building of a string array from the stack trace, the error log, and the return code on Failure.

Having the flow be a standalone variable is not required, but is useful for making the code readable.

#Assignemnt 2: Post Method and Stream Processing
Create a `post` method handler route called `helloJson` that uses Akka Streams to write the payload to a temp file and inform the client of the number of bytes written.
- The method should only accept messages 40 bytes in size.
- Create a flow of the type `[ByteString, ByteString, _]`
- map the flow `Flow[ByteString].map{}` so that the ByteString is concatenated with an additional message that says "more data"
- The total size of the combined message must be 49 bytes
- Build a sink of the type `[ByteString, Future[IOResult]]`
- Create a temp file for the sink to use with the `Files.createTempFile()` method
- The return message must be "Finished writing data: 49 bytes written to file"

#Security and Server Settings
In addition to the size constraint, Akka Http is able to handle authentication headers, TLS, and connection throttling to prevent overloading the system.
##Authentication
There are two main types of authentication supported:
- Basic
- OAUTH
We will only cover Basic in this module. 

Basic authentication is a non-encrypted value sent in the header so it is critical to have TLS (aka HTTPS) setup first. The pattern in curl is:
`-H 'Authentication: Basic <UTF-8 String with optional user and required password>'`
Akka HTTP has helper methods to extract and validate the credentials. A method that requires authentication must have a helper function to check the validity of the password. The route handler looks like this:
```
authenticateBasic(realm = "any string", authenticator<implied password is sent here as _> {
            case x: String if x == "<the username from the authentication header>" => <do your work here> complete(StatusCodes.OK)
            case _ => complete(StatusCodes.Unauthorized)
```
Two values must be supplied: a realm (can be any string if you aren't using it) and an `Authenticator` type. The `Authenticator` is best setup as an external function that takes in `Credentials` and returns an `Option[String]`. For example:
```  def authenticator(credentials: Credentials): Option[String] = {
    credentials match {
      case p @ Credentials.Provided(id) if p.verify("somepass") => Some(id)
      case _ => log.error(credentials.toString); Some("unauthorized")
    }
  }
```
More elaborate setups can use the authenticator function to query a database or LDAP for verification.
##HTTPS/TLS
Passwords are at risk without HTTPS! This is one of the more frustrating and complex items for any REST api and it is often skipped during development. I'll go through the steps here since the certificates must be added to cURL in order for tests to work.

First, you must have the Java tool `keytool` installed. This will run the encryption and key generation functions. By default, it comes with your Java installation. It is also helpful to have a random password generator, although you may also use any string you want.

The three main steps are:
1. Generate your own Certificate Authority
2. Trust your own CA
3. Generate and sign your server certificate using that CA
4. Load the self-signed certificate and create an HTTPS listener

I'll mostly follow the Lightbend [walkthrough](https://lightbend.github.io/ssl-config/CertificateGeneration.html) for this.

First, generate your password however you want to do that. 

Second, generate your self-signed root CA and export the certificate for the next steps. Remember to do this in a directory where you can get the output files. I recommend using `src-->main-->resources`. In this example, your password is taken from the environment variables. You also have the option to enter it manually or read it from a file. Use the `--help` option to see the syntax for those:
```
keytool -genkeypair -v \
  -alias exampleca \
  -dname "CN=exampleCA, OU=Technology Org, O=Urdnot Co., L=Seattle, ST=Washington, C=US" \
  -keystore exampleca.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg RSA \
  -keysize 4096 \
  -ext KeyUsage:critical="keyCertSign" \
  -ext BasicConstraints:critical="ca:true" \
  -validity 9999
```
Now, export the exampleCA public certificate as exampleca.crt and put it into your Java keystore (again, using the environment variables for your password in this example).
```
keytool -export -v \
  -alias exampleca \
  -file exampleca.crt \
  -keypass:env PW \
  -storepass:env PW \
  -keystore exampleca.jks \
  -rfc
  ```
Step three is more involved, you must generate a certificate, a certificate signing request, sign it, then import the signed certificate into your keystore.

First, create a server certificate, tied to example.com
```
keytool -genkeypair -v \
  -alias example.com \
  -dname "CN=exampleCA, OU=Technology Org, O=Urdnot Co., L=Seattle, ST=Washington, C=US" \
  -keystore example.com.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg RSA \
  -keysize 2048 \
  -validity 385
```
Next, create the certificate signing request
```
keytool -certreq -v \
  -alias example.com \
  -keypass:env PW \
  -storepass:env PW \
  -keystore example.com.jks \
  -file example.com.csr
```
Then, sign the new certificate with your previously generated root CA
```
keytool -gencert -v \
  -alias exampleca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore exampleca.jks \
  -infile example.com.csr \
  -outfile example.com.crt \
  -ext KeyUsage:critical="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth" \
  -ext SAN="DNS:example.com" \
  -rfc
```
Then, add your root CA as a trusted root
```
keytool -import -v \
  -alias exampleca \
  -file exampleca.crt \
  -keystore example.com.jks \
  -storetype JKS \
  -storepass:env PW << EOF
yes
EOF
```
Finally, import your new certificate into your truststore.
``` 
keytool -import -v \
  -alias example.com \
  -file example.com.crt \
  -keystore example.com.jks \
  -storetype JKS \
  -storepass:env PW
```
Check that your certificate is added
```
keytool -list -v \
  -keystore example.com.jks \
  -storepass:env PW
 ```
at this point, you should have new files in your resources directory:
1. example.com.crt 
2. example.com.csr
3. example.com.jks
4. exampleca.crt
5. exampleca.jks
6. password

If you need to re-run any steps, the file `password` has the password you will need.

Once this is setup, it's time to listen for TLS/HTTPS connections!
### Setup TLS in the server code
Setting up TLS is a multi-step process. After you have the keystore with the self-signed certificate you will need to add a new port to your application.conf file:
`akka.server.https.port`
Then load it as an int: `private val httpsPort: Int = config.getInt("akka.server.https.port")`

Now, the reason the resources directory is good for holding these files is it is easy to access for the different setup steps. First, get the password saved in the password file. I won't pretend this isn't a completely stupid number of steps you have to take:
`val password = getClass.getClassLoader.getResourceAsStream("password").readAllBytes().map(_.toChar).mkString.trim.toCharArray`
And this is the post Java 9 version. Earlier versions were worse!

Remember the "trim" part, that is useful if you are reading from a file or an env variable.

Next, initiate the truststore's keystore and the actual keystore (I know, I know, it's the result of too many committees over too many years):
```
val ks: KeyStore = KeyStore.getInstance("PKCS12")
val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("example.com.jks")
```
It's best to check that you have the first steps working before moving on:
```  // require it before loading
  require(keystore != null, "Keystore required!")
  ks.load(keystore, password)
```
Now you have your local certificate loaded, time to inistantiate the keystore:
```
val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
keyManagerFactory.init(ks, password)
```

Next, the truststore:
```
val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
tmf.init(ks)
```
FINALLY you can instantiate the TLS for your code:
```
val sslContext: SSLContext = SSLContext.getInstance("TLS")
sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
val https: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)
```

Now, did you wonder why we built HTTP as a list? This is why: you can have both HTTP and HTTPS listen at the same time!
The new HTTP + HTTPS setup will be:
```
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
```
And there you have it! TLS enabled, security achieved (except the NSA, they can read anything).

##Connection Handling
The final concept in the REST API module are the set of configurations that are notable for the server. The [akka-http configuration site](https://doc.akka.io/docs/akka-http/current/configuration.html) has the full documentation. Here I will discuss a few that I have found useful.

1. 
#Assignment 3: Security and Request Throttling
