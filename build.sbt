import com.typesafe.sbt.packager.docker._
import sbt.Keys.mappings

name := "basicrestapi"

version := "0.4.3"

organization := "com.urdnot.api"

val scalaMajorVersion = "2.13"
val scalaMinorVersion = "5"

scalaVersion := scalaMajorVersion.concat("." + scalaMinorVersion)

packageName in Docker := packageName.value

libraryDependencies ++= {
  val akkaVersion = "2.6.13"
  val akkaHttpVersion = "10.2.4"
  val scalaLoggingVersion = "3.9.2"
  val logbackVersion = "1.2.3"
  val scalaTestVersion = "3.2.5"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )
}

enablePlugins(DockerPlugin)

assemblyJarName := s"${name.value}.v${version.value}.jar"
val meta = """META.INF(.)*""".r

mappings in(Compile, packageBin) ~= {
  _.filterNot {
    case (_, name) => Seq("application.conf").contains(name)
  }
}

assemblyMergeStrategy in assembly := {
  case n if n.endsWith(".properties") => MergeStrategy.concat
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("resources/application.conf") => MergeStrategy.discard
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}
dockerBuildOptions += "--no-cache"
dockerUpdateLatest := true
dockerPackageMappings in Docker += file(s"target/scala-2.13/${assemblyJarName.value}") -> s"opt/docker/${assemblyJarName.value}"
mappings in Docker += file("src/main/resources/application.conf") -> "opt/docker/application.conf"
mappings in Docker += file("src/main/resources/logback.xml") -> "opt/docker/logback.xml"
dockerExposedPorts := Seq(80, 443)
maintainer in Docker := "Jeffrey Sewell"

dockerCommands := Seq(
  Cmd("FROM", "openjdk:14-jdk-slim"),
//  Cmd("LABEL", s"""MAINTAINER="Jeffrey Sewell""""),
  Cmd("COPY", s"opt/docker/${assemblyJarName.value}", s"/opt/docker/${assemblyJarName.value}"),
  Cmd("COPY", "opt/docker/application.conf", "/var/application.conf"),
  Cmd("COPY", "opt/docker/logback.xml", "/var/logback.xml"),
  Cmd("ENV", "CLASSPATH=/opt/docker/application.conf:/opt/docker/logback.xml"),
  Cmd("ENV", "JKS_PW=''"),
  Cmd("ENV", "HTTP_PORT=80"),
  Cmd("ENTRYPOINT", s"java -cp /opt/docker/${assemblyJarName.value} ${mainClass.in(Compile).value.get}")
)
// sbt clean && sbt assembly && sbt docker:publishLocal
// docker tag basicrestapi:latest intel-server-03:5000/basicrestapi:latest
// docker push intel-server-03:5000/basicrestapi:latest