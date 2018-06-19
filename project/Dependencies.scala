/*
 * Copyright 2018 Exon IT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._

object Dependencies {
  val slf4jApi         : ModuleID = "org.slf4j" % "slf4j-api" % "1.7.25"
  val slf4jJdk14       : ModuleID = "org.slf4j" % "slf4j-jdk14" % "1.7.25"
  val jodaTime         : ModuleID = "joda-time" % "joda-time" % "2.10"
  val jodaConvert      : ModuleID = "org.joda" % "joda-convert" % "2.1"
  val play25Ws         : ModuleID = "com.typesafe.play" %% "play-ws" % "2.5.18"
  val play26Ws         : ModuleID = "com.typesafe.play" %% "play-ahc-ws" % "2.6.15"
  val play27Ws         : ModuleID = "com.typesafe.play" %% "play-ahc-ws" % "2.7.0-M1"
  val playWsStandalone : ModuleID = "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.9"
  val playWsStandalone2: ModuleID = "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.0-M2"
  val json4sCore       : ModuleID = "org.json4s" %% "json4s-core" % "3.5.4"
  val json4sJackson    : ModuleID = "org.json4s" %% "json4s-jackson" % "3.5.4"
  val scalaLogging     : ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
  val scalatest        : ModuleID = "org.scalatest" %% "scalatest" % "3.0.5"
  val restClientDriver : ModuleID = "com.github.rest-driver" % "rest-client-driver" % "2.0.0"
  val catsFree         : ModuleID = "org.typelevel" %% "cats-free" % "0.9.0"
  val scalaArm         : ModuleID = "com.jsuereth" %% "scala-arm" % "2.0"
  val monixEval        : ModuleID = "io.monix" %% "monix-eval" % "2.3.3"
  val enumeratum       : ModuleID = "com.beachape" %% "enumeratum" % "1.5.13"
}
