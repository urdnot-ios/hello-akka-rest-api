
ThisBuild / name := "simple-akka-rest-api"
ThisBuild / version := "0.7.1"
ThisBuild / organization := "com.urdnot.api"
val scalaMajorVersion = "2.13"
val scalaMinorVersion = "8"
ThisBuild / scalaVersion := scalaMajorVersion.concat("." + scalaMinorVersion)

lazy val root = (project in file("."))
  .aggregate(module01Student, module01Key, module02Student, module02Key, module03Student, module03Key)
  .settings(name := "simple-akka-rest-api",
    sharedSettings
  )
lazy val module00Student = (project in file("module00 - Student")).settings(sharedSettings)
lazy val module00Key = (project in file("module00 - Key")).settings(sharedSettings)
lazy val module01Student = (project in file("module01 - Student")).settings(sharedSettings)
lazy val module01Key = (project in file("module01 - Key")).settings(sharedSettings)
lazy val module02Student = (project in file("module02 - Student")).settings(sharedSettings)
lazy val module02Key = (project in file("module02 - Key")).settings(sharedSettings)
lazy val module03Student = (project in file("module03 - Student")).settings(sharedSettings)
lazy val module03Key = (project in file("module03 - Key")).settings(sharedSettings)


lazy val sharedSettings = Seq(
  libraryDependencies ++= {
    val AkkaVersion = "2.6.19"
    val AkkaHttpVersion = "10.2.9"
    val AkkaStreamKafkaVersion = "3.0.0"
    val AlpakkaUdpVersion = "3.0.4"
    val circeVersion = "0.14.1"
    val JacksonVersion = "2.13.2.2"
    val LogbackClassicVersion = "1.2.11"
    val scalaLoggingVersion = "3.9.4"
    val ScalaTestVersion = "3.2.11"
    val TestcontainersVersion = "1.16.3"

    Seq(
      "ch.qos.logback" % "logback-classic" % LogbackClassicVersion,

      "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,

      "com.lightbend.akka" %% "akka-stream-alpakka-udp" % AlpakkaUdpVersion,

      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-kafka" % AkkaStreamKafkaVersion,
      "com.typesafe.akka" %% "akka-stream-kafka-testkit" % AkkaStreamKafkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test",
      "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,

      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-optics" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",

      "org.testcontainers" % "kafka" % TestcontainersVersion % Test
    )
  }
)
