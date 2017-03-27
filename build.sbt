import com.atlassian.labs.gitstamp.GitStampPlugin._

lazy val repo: Option[String] = sys.props.get("publishTo")

lazy val si2712 =
  scalacOptions ++=
    (if (CrossVersion.partialVersion(scalaVersion.value).exists(_._2 >= 12)) Seq("-Ypartial-unification")
    else Seq())

lazy val commonSettings = Seq(
  organization := "by.exonit.redmine.client",
  organizationName := "Exon IT",
  organizationHomepage := Some(url("http://exonit.by")),

  startYear := Some(2015),

  version := "4.0.0-SNAPSHOT",

  scalaVersion := "2.11.8",

  si2712,
  libraryDependencies ++= Dependencies.si2712(scalaVersion.value),

  publishMavenStyle := true,
  pomIncludeRepository := {_ => false},
  publishTo := repo.map {r =>
    if (isSnapshot.value) {
       "snapshots" at r
    } else {
      "releases" at r
    }
  },
  credentials ++= sys.props.get("credentialPath").map {cp =>
    cp.split(',').map {path => Credentials(file(path))}.toSeq
  } getOrElse Seq.empty
) ++ Seq(gitStampSettings: _*)

lazy val `client-api` = (project in file("client-api")).
  settings(commonSettings: _*).
  settings(
    name := "client-api",
    description := s"Redmine Scala Client API for Scala ${scalaBinaryVersion.value}",
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
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
    name := "client-core",
    description := s"Redmine Scala Client Core for Scala ${scalaBinaryVersion.value}",
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
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

lazy val `client-play-ws` = (project in file("client-play-ws")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := "client-play-ws",
    description := s"Redmine Scala Client Play-WS Implementation for Scala ${scalaBinaryVersion.value}",
    libraryDependencies ++= Seq(
      Dependencies.playWs,
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
    name := "client-parent",
    description := s"Redmine Scala Client Parent for Scala ${scalaBinaryVersion.value}"
  ).
  aggregate(`client-api`, `client-core`, `client-play-ws`).
  enablePlugins(CrossPerProjectPlugin)

