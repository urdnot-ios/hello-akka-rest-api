import sbt.Keys.resolvers

resolvers ++= Seq(
  "JFrog Repository" at "https://scala.jfrog.io/shmishleniy/"
)
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")

