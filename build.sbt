
ThisBuild / name := "simple-akka-rest-api"
ThisBuild / version := "0.5.1"
ThisBuild / organization := "com.urdnot.api"
val scalaMajorVersion = "2.13"
val scalaMinorVersion = "5"
ThisBuild / scalaVersion :=  scalaMajorVersion.concat("." + scalaMinorVersion)

lazy val root = (project in file ("."))
  .aggregate(module01, module02Key, module02Student)
  .settings( name := "simple-akka-rest-api",
    sharedSettings
  )
lazy val module01 = (project in file("module01")).settings(sharedSettings)
lazy val module02Key = (project in file("module02 - key")).settings(sharedSettings)
lazy val module02Student = (project in file("module02 - Student")).settings(sharedSettings)
lazy val module03 = (project in file("module03")).settings(sharedSettings)



lazy val sharedSettings = Seq(
  libraryDependencies ++= {
  val akkaVersion = "2.6.13"
  val akkaHttpVersion = "10.2.4"
  val scalaLoggingVersion = "3.9.2"
  val logbackVersion = "1.2.3"
  val scalaTestVersion = "3.2.5"
  val circeVersion = "0.13.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-optics" % circeVersion
  )
}
)
