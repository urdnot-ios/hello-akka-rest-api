
ThisBuild / name := "simple-akka-rest-api"
ThisBuild / version := "0.6.5"
ThisBuild / organization := "com.urdnot.api"
val scalaMajorVersion = "2.13"
val scalaMinorVersion = "5"
ThisBuild / scalaVersion := scalaMajorVersion.concat("." + scalaMinorVersion)

lazy val root = (project in file("."))
  .aggregate(module01Student, module01Key, module02Student, module02Key, module03Student, module03Key)
  .settings(name := "simple-akka-rest-api",
    sharedSettings
  )
lazy val module01Student = (project in file("module01 - Student")).settings(sharedSettings)
lazy val module01Key = (project in file("module01 - Key")).settings(sharedSettings)
lazy val module02Student = (project in file("module02 - Student")).settings(sharedSettings)
lazy val module02Key = (project in file("module02 - Key")).settings(sharedSettings)
lazy val module03Student = (project in file("module03 - Student")).settings(sharedSettings)
lazy val module03Key = (project in file("module03 - Key")).settings(sharedSettings)


lazy val sharedSettings = Seq(
  libraryDependencies ++= {
    val AkkaVersion = "2.6.16"
    val AkkaHttpVersion = "10.2.6"
    val AkkaStreamKafkaVersion = "2.1.1"
    val circeVersion = "0.14.1"
    val JacksonVersion = "2.12.5"
    val LogbackClassicVersion = "1.2.6"
    val scalaLoggingVersion = "3.9.4"
    val ScalaTestVersion = "3.2.9"
    val TestcontainersVersion = "1.16.0"

    Seq(
      "ch.qos.logback" % "logback-classic" % LogbackClassicVersion,

      "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",

      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-kafka" % AkkaStreamKafkaVersion,
      "com.typesafe.akka" %% "akka-stream-kafka-testkit" % AkkaStreamKafkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test",
      "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-optics" % circeVersion,
      "org.testcontainers" % "kafka" % TestcontainersVersion % Test
    )
  }
)
