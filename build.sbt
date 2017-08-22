
import com.atlassian.labs.gitstamp.GitStampPlugin._

lazy val repo: Option[String] = sys.props.get("publishTo")

lazy val commonSettings = Seq(
  organization := "by.exonit.redmine.client",
  organizationName := "Exon IT",
  organizationHomepage := Some(url("http://exonit.by")),

  startYear := Some(2015),

  version := "5.0.0-SNAPSHOT",

  scalaVersion := "2.11.11",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",       // yes, this is 2 args
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture"
  ),

  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.4" cross CrossVersion.binary),

  publishMavenStyle := true,
  bintrayOrganization := Some("exon-it"),
  bintrayRepository := "maven-releases",
  bintrayReleaseOnPublish in ThisBuild := false,

  pomIncludeRepository := {_ => false},
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("https://github.com/exon-it/redmine-scala-client")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/exon-it/redmine-scala-client"),
    "scm:git:https://github.com/exon-it/redmine-scala-client.git",
    Some("scm:git:git@github.com:exon-it/redmine-scala-client.git"))),
  pomExtra := <developers>
    <developer>
      <id>antonov_i</id>
      <name>Igor Antonov</name>
      <email>antonov_i@exon-it.by</email>
      <organization>Exon IT</organization>
      <organizationUrl>http://exonit.by</organizationUrl>
      <timezone>Europe/Minsk</timezone>
    </developer>
  </developers>,
  credentials ++= sys.props.get("credentialPath").map {cp =>
    cp.split(',').map {path => Credentials(file(path))}.toSeq
  } getOrElse Seq.empty
) ++ Seq(gitStampSettings: _*)

lazy val `client-api` = (project in file("client-api")).
  settings(commonSettings: _*).
  settings(
    name := s"client-api",
    description := s"Redmine REST API Client for Scala ${scalaBinaryVersion.value}: Client API. " +
      s"Contains domain classes and API manager traits.",
    crossScalaVersions := Seq("2.11.11", "2.12.3"),
    libraryDependencies ++= Seq(
      Dependencies.monixEval,
      Dependencies.catsFree,
      Dependencies.jodaTime,
      Dependencies.jodaConvert,
      Dependencies.json4sCore,
      Dependencies.scalaLogging,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.junit % Test,
      Dependencies.scalatest % Test
    )
  )

lazy val `client-core` = (project in file("client-core")).
  dependsOn(`client-api`).
  settings(commonSettings: _*).
  settings(
    name := s"client-core",
    description := s"Redmine REST API Client for Scala ${scalaBinaryVersion.value}: Client Core. " +
      s"Contains API manager and JSON serialization implementation.",
    crossScalaVersions := Seq("2.11.11", "2.12.3"),
    libraryDependencies ++= Seq(
      Dependencies.catsFree,
      Dependencies.jodaTime,
      Dependencies.jodaConvert,
      Dependencies.json4sCore,
      Dependencies.json4sJackson,
      Dependencies.slf4jApi,
      Dependencies.scalaLogging,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.junit % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test
    )
  )

lazy val `client-play25-ws` = (project in file("client-play25-ws")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := s"client-play25-ws",
    description := s"Redmine REST API Client for Scala ${scalaBinaryVersion.value}: Play-WS 2.5 Web Client",
    crossScalaVersions := Seq("2.11.11"),
    libraryDependencies ++= Seq(
      Dependencies.play25Ws,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.junit % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-play26-ws` = (project in file("client-play26-ws")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := s"client-play26-ws",
    description := s"Redmine REST API Client for Scala ${scalaBinaryVersion.value}: Play-WS 2.6 Web Client",
    crossScalaVersions := Seq("2.11.11","2.12.3"),
    libraryDependencies ++= Seq(
      Dependencies.play26Ws,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.junit % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-play-ws-standalone` = (project in file("client-play-ws-standalone")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := s"client-play-ws-standalone",
    description := s"Redmine REST API Client for Scala ${scalaBinaryVersion.value}: Play-WS Standalone Web Client",
    crossScalaVersions := Seq("2.11.11", "2.12.3"),
    libraryDependencies ++= Seq(
      Dependencies.playWsStandalone,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.junit % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-parent` = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := s"client-parent",
    description := s"Redmine REST API Client for Scala ${scalaBinaryVersion.value}: Parent POM. Used for grouping sub-projects.",
    // Do not publish root project
    publishArtifact := false
  ).
  aggregate(`client-api`, `client-core`, `client-play25-ws`, `client-play26-ws`, `client-play-ws-standalone`).
  enablePlugins(CrossPerProjectPlugin)

