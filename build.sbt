import com.typesafe.sbt.packager.docker._

name := "simple-akka-rest-api"

version := "0.2.3"

organization := "com.urdnot.api"

val scalaMajorVersion = "2.13"
val scalaMinorVersion = "1"

scalaVersion := scalaMajorVersion.concat("." + scalaMinorVersion)

mainClass in assembly := Some("com.urdnot.api.SimpleAkkaRestApi")

libraryDependencies ++= {
  val akkaVersion = "2.6.3"
  val akkaHttpVersion = "10.1.11"
  val akkaSprayJsonVersion = "10.1.11"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaSprayJsonVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.8" % "test"
  )
}

enablePlugins(DockerPlugin)

//Name the fat jar:
assemblyJarName := s"${name.value}.v${version.value}.jar"

val meta = """META.INF(.)*""".r
assemblyMergeStrategy in assembly := {
  case n if n.endsWith(".properties") => MergeStrategy.concat
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("resources/application.conf") => MergeStrategy.discard
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}

// Build the docker image and add the fat jar
dockerBuildOptions += "--no-cache"
dockerUpdateLatest := true
dockerPackageMappings in Docker += file(s"target/scala-${scalaMajorVersion}/${assemblyJarName.value}") -> s"opt/docker/${assemblyJarName.value}"
mappings in Docker += file("src/main/resources/application.conf") -> "opt/docker/application.conf"
mappings in Docker += file("security/keystore.jks") -> "opt/docker/keystore.jks"
mappings in Docker += file("security/keystore_pass.txt") -> "opt/docker/keystore_pass.txt"
dockerExposedPorts in Docker := Seq(9443)
maintainer in Docker := "Jeffrey Sewell"
dockerEntrypoint in Docker := Seq("java", "com.urdnot.api.SimpleAkkaRestApiApp")
dockerAlias := com.typesafe.sbt.packager.docker.DockerAlias(
  registryHost = None,
  username = None,
  name = "intel-server-03:5000/basicrestapi/latest",
  tag = Option("basicRestApi"))

dockerCommands := Seq(
  Cmd("FROM", "openjdk:8-alpine"),
  Cmd("LABEL", s"""MAINTAINER="Jeffrey Sewell""""),
  Cmd("ENV", "CLASSPATH=$CLASSPATH:/opt/lib/*.jar"),
  Cmd("COPY", s"opt/docker/keystore.jks /etc/security/keystore.jks"),
  Cmd("COPY", s"opt/docker/keystore_pass.txt /etc/security/keystore_pass.txt"),
  Cmd("COPY", s"opt/docker/${assemblyJarName.value}", s"/opt/lib/${assemblyJarName.value}"),
  Cmd("EXPOSE", "9443"),
  Cmd("ENTRYPOINT", s"java -cp /opt/lib/${assemblyJarName.value} com.urdnot.api.SimpleAkkaRestApiApp")
)


