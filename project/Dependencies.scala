import sbt._

object Dependencies {
  val junit            : ModuleID = "junit" % "junit" % "4.12"
  val slf4jApi         : ModuleID = "org.slf4j" % "slf4j-api" % "1.7.25"
  val slf4jJdk14       : ModuleID = "org.slf4j" % "slf4j-jdk14" % "1.7.25"
  val jodaTime         : ModuleID = "joda-time" % "joda-time" % "2.9.9"
  val jodaConvert      : ModuleID = "org.joda" % "joda-convert" % "1.8.2"
  val play25Ws         : ModuleID = "com.typesafe.play" %% "play-ws" % "2.5.16"
  val play26Ws         : ModuleID = "com.typesafe.play" %% "play-ahc-ws" % "2.6.3"
  val playWsStandalone : ModuleID = "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.4"
  val json4sCore       : ModuleID = "org.json4s" %% "json4s-core" % "3.5.3"
  val json4sJackson    : ModuleID = "org.json4s" %% "json4s-jackson" % "3.5.3"
  val scalaLogging     : ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  val scalatest        : ModuleID = "org.scalatest" %% "scalatest" % "3.0.3"
  val restClientDriver : ModuleID = "com.github.rest-driver" % "rest-client-driver" % "2.0.0"
  val catsFree         : ModuleID = "org.typelevel" %% "cats-free" % "1.0.0-MF"
  val scalaArm         : ModuleID = "com.jsuereth" %% "scala-arm" % "2.0"
  val monixEval        : ModuleID = "io.monix" %% "monix-eval" % "2.3.0"
}
