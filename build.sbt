
lazy val repo: Option[String] = sys.props.get("publishTo")

lazy val commonSettings = Seq(
  organization := "by.exonit.redmine.client",
  organizationName := "Exon IT",
  organizationHomepage := Some(url("http://exonit.by")),

  startYear := Some(2015),

  version := "6.0.0-M2",

  scalaOrganization := "org.typelevel",
  scalaVersion := "2.11.11-bin-typelevel-4",

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
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Xfuture",
    // Typelevel Scala 4
    "-Yinduction-heuristics",       // speeds up the compilation of inductive implicit resolution
//    "-Ykind-polymorphism",          // type and method definitions with type parameters of arbitrary kinds
    "-Yliteral-types",              // literals can appear in type position
    "-Xstrict-patmat-analysis",     // more accurate reporting of failures of match exhaustivity
    "-Xlint:strict-unsealed-patmat" // warn on inexhaustive matches against unsealed traits
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) if minor >= 12 => Seq(
      "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
      "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals",              // Warn if a local definition is unused.
      "-Ywarn-unused:params",              // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates",            // Warn if a private member is unused.
      "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
    )
    case _ => Nil
  }),

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
  } getOrElse Seq.empty,
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.7" cross CrossVersion.binary)
)

lazy val `client-api` = (project in file("client-api")).
  settings(commonSettings: _*).
  settings(
    name := s"client-api",
    description := "Redmine REST API Client for Scala: Client API. " +
      "Contains domain classes and API manager traits.",
    crossScalaVersions := Seq("2.11.11-bin-typelevel-4", "2.12.4-bin-typelevel-4"),
    libraryDependencies ++= Seq(
      Dependencies.catsFree,
      Dependencies.catsEffect,
      Dependencies.jodaTime,
      Dependencies.jodaConvert,
      Dependencies.json4sCore,
      Dependencies.scalaLogging,
      Dependencies.enumeratum,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.scalatest % Test
    )
  )

lazy val `client-core` = (project in file("client-core")).
  dependsOn(`client-api`).
  settings(commonSettings: _*).
  settings(
    name := "client-core",
    description := "Redmine REST API Client for Scala: Client Core. " +
      "Contains API manager and JSON serialization implementation.",
    crossScalaVersions := Seq("2.11.11-bin-typelevel-4", "2.12.4-bin-typelevel-4"),
    libraryDependencies ++= Seq(
      Dependencies.catsFree,
      Dependencies.jodaTime,
      Dependencies.jodaConvert,
      Dependencies.json4sCore,
      Dependencies.json4sJackson,
      Dependencies.slf4jApi,
      Dependencies.scalaLogging,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test
    )
  )

lazy val `client-play25-ws` = (project in file("client-play25-ws")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := "client-play25-ws",
    description := "Redmine REST API Client for Scala: Play-WS 2.5 Web Client",
    crossScalaVersions := Seq("2.11.11-bin-typelevel-4"),
    libraryDependencies ++= Seq(
      Dependencies.play25Ws,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-play26-ws` = (project in file("client-play26-ws")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := "client-play26-ws",
    description := "Redmine REST API Client for Scala: Play-WS 2.6 Web Client",
    crossScalaVersions := Seq("2.11.11-bin-typelevel-4","2.12.4-bin-typelevel-4"),
    libraryDependencies ++= Seq(
      Dependencies.play26Ws,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-play27-ws` = (project in file("client-play27-ws")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := "client-play27-ws",
    description := "Redmine REST API Client for Scala: Play-WS 2.7 Web Client",
    crossScalaVersions := Seq("2.11.11-bin-typelevel-4","2.12.4-bin-typelevel-4"),
    libraryDependencies ++= Seq(
      Dependencies.play27Ws,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-play-ws-standalone` = (project in file("client-play-ws-standalone")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := "client-play-ws-standalone",
    description := "Redmine REST API Client for Scala: Play-WS Standalone 1.x Web Client",
    crossScalaVersions := Seq("2.11.11-bin-typelevel-4", "2.12.4-bin-typelevel-4"),
    libraryDependencies ++= Seq(
      Dependencies.playWsStandalone,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-play-ws-standalone-2` = (project in file("client-play-ws-standalone-2")).
  dependsOn(`client-core`).
  settings(commonSettings: _*).
  settings(
    name := "client-play-ws-standalone-2",
    description := "Redmine REST API Client for Scala: Play-WS Standalone 2.x Web Client",
    crossScalaVersions := Seq("2.11.11-bin-typelevel-4", "2.12.4-bin-typelevel-4"),
    libraryDependencies ++= Seq(
      Dependencies.playWsStandalone2,
      Dependencies.slf4jJdk14 % Test,
      Dependencies.scalatest % Test,
      Dependencies.restClientDriver % Test,
      Dependencies.scalaArm % Test
    )
  )

lazy val `client-parent` = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "client-parent",
    description := "Redmine REST API Client for Scala: Parent POM. Used for grouping sub-projects.",
    // Do not publish root project
    publishArtifact := false
  ).
  aggregate(
    `client-api`,
    `client-core`,
    `client-play25-ws`,
    `client-play26-ws`,
    `client-play27-ws`,
    `client-play-ws-standalone`,
    `client-play-ws-standalone-2`
  )
