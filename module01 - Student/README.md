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
1. Take a TCP socket on the host operating system and listen for connections
2. Accept the connection
3. Parse the route of the connection and pass the HTTP entity to that route for processing
4. When the route is finished processing, it calls `complete()` and inside provides the correct reply message

So let's look at each step. 

##Preparation
First, make sure you have a `module01-->src-->resources` folder. Inside that folder, create two files:
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

The syntax `${?HTTP_PORT}` is used to obtain the variable data from either an environment variable or a variable set inside the file. This allows for local testing but then is transparent when the code is deployed because the file variables will be overwritten by the production environment variables. 

With `build.sbt`, `application.conf`, and `logback.xml` setup, it's time to write some code!

The first two steps in setting up an Akka HTTP REST api are to build the routes and then set up the listener.

With this information, you can build a URL that will listen for connections. For example, it might look like this:

`http://localhost:8181`

However, there is still one more step to route the connection to the appropriate processing code.

##Routes
Routes are appended to the end of a url and port. The server must have routes setup so that messages can be delivered correctly. The different types of routes accept data in different, but overlapping, ways. 

A route is identified using a [query string](https://en.wikipedia.org/wiki/Query_string) and will look something like this:

`http://localhost:8181?myRoute`

Notice the `?` that begins the query string

The two most common ways to send data to a route are either as "Parameters" or as "Data". 

In Akka HTTP there are a suite of built-in Directives that help parsing and processing the data. We will only cover a few here but the full documentation is [here](https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/)
###Parameters
Paremeters are sent in the URL itself using key/value pairs. They will look something like this:
`http://localhost:8181?myRoute&key1=val1&key2=val2` 

Notice the use of the ampersand `&` to distinguish sets of keys and values and the equal sign `=` to connect a key with a value. The `&` is not mandatory on all sites, but it is the default for Akka HTTP.

Any text must be [percent encoded](https://en.wikipedia.org/wiki/Percent-encoding) so that things like whitespace or apostrophies will be represented using `%20` format (percent sign followed by 2 digits).

The query parameters are quick and easy but not very flexible for more complex data. While there is no technical limit on the amount of data you can send in a query string, most browsers and servers have a built-in limit.

The limit is first (and optionally) set in the `application.conf` file as `akka.http.parsing.max-content-length`. A second location to set the limit is the `withSizeLimit` directive that processes the message. 

###Type Safety
It is important to start routes with Type Safety. This means that inbound data--parameters or binary--should be converted to known Classes on entry. See examples [here](https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/index.html). Always remember to try/catch the inbound data into a type Class that you will use.
###Notes
The built in path Directives are:
- post
- get
- put
- patch
- delete
- head
- options

See [w3.org rfc2616](https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html) for more details

We will test `post` and `get` in this module.
#Assignment 1: Setup and `get()`
- Create your first route inside an object called `ApiHelloRoutes` Include inside it:
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

#Post and Binary Data
##Data
##Using Akka Streams to Manage the Data 
#Assignemnt 2: Post Method and Stream Processing

#Security and Server Settings
##Authentication
##HTTPS/TLS
##Connection Throttling
#Assignment 3: Security and Request Throttling