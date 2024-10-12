import sbt.Keys.test

ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.1.3"

lazy val tiff413       = "com.tiff413"
lazy val scala3Version = "3.3.1"

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// Common - contains domain model
///////////////////////////////////////////////////////////////////////////////////////////////////////////

lazy val circeVersion        = "0.14.1"
lazy val circeLiteralVersion = "0.14.9"
lazy val zerowasteVersion   = "0.2.21"

lazy val core = (crossProject(JSPlatform, JVMPlatform) in file("common"))
  .settings(
    name         := "common",
    scalaVersion := scala3Version,
    organization := tiff413,
    libraryDependencies ++= Seq(
      // circe
      "io.circe" %%% "circe-core"    % circeVersion,
      "io.circe" %%% "circe-parser"  % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-refined" % circeVersion,
      "io.circe" %%% "circe-literal" % circeLiteralVersion,
      compilerPlugin("com.github.ghik" % "zerowaste" % zerowasteVersion cross CrossVersion.full)
    ),
    scalacOptions += "-Werror",
    Compile / compile := {
      println("Running scalafmtAll")
      (Compile / compile).dependsOn(Compile / scalafmtAll).value
    },
    test := {
      println("Running dependency checker")
      undeclaredCompileDependencies.value
      unusedCompileDependencies.value
      println("Running scalafmtCheckAll")
      scalafmtCheckAll.value
    }
  )
  .jvmSettings()
  .jsSettings()

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// Frontend
///////////////////////////////////////////////////////////////////////////////////////////////////////////

lazy val laikaVersion  = "0.19.0"
lazy val fs2DomVersion = "0.1.0"
lazy val tyrianVersion = "0.6.1"

lazy val app = (project in file("app"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name         := "app",
    scalaVersion := scala3Version,
    organization := tiff413,
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "tyrian-io"  % tyrianVersion,
      "com.armanbilge"  %%% "fs2-dom"    % fs2DomVersion,
      "org.planet42"    %%% "laika-core" % laikaVersion
    ),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    semanticdbEnabled := true,
    autoAPIMappings   := true
  )
  .dependsOn(core.js)

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// Backend
///////////////////////////////////////////////////////////////////////////////////////////////////////////
lazy val catsEffectVersion          = "3.3.14"
lazy val catsVersion                = "2.12.0"
lazy val doobieVersion              = "1.0.0-RC1"
lazy val enumVersion                = "1.7.4"
lazy val http4sVersion              = "0.23.27"
lazy val http4sMunitVersion         = "1.1.0"
lazy val javaMailVersion            = "1.6.2"
lazy val log4catsVersion            = "2.7.0"
lazy val logbackVersion             = "1.5.6"
lazy val munitCatsEffectVersion     = "2.0.0"
lazy val munitVersion               = "1.0.0"
lazy val pureConfigVersion          = "0.17.7"
lazy val refinedVersion             = "0.11.2"
lazy val scalaTestCatsEffectVersion = "1.5.0"
lazy val scalaTestVersion           = "3.2.18"
lazy val slf4jVersion               = "2.0.13"
lazy val stripeVersion              = "22.12.0"
lazy val testContainerVersion       = "1.20.0"
lazy val tsecVersion                = "0.4.0"

lazy val server = (project in file("server"))
  .settings(
    name         := "server",
    scalaVersion := scala3Version,
    organization := tiff413,
    libraryDependencies ++= Seq(
      // cats
      "org.typelevel" %% "cats-core"   % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,

      // http4s
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-circe"        % http4sVersion,

      // logging
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "org.slf4j"      % "slf4j-simple"   % slf4jVersion,

      // misc
      "io.circe"              %% "circe-fs2"       % circeVersion,
      "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
      "eu.timepit"            %% "refined"         % refinedVersion,
      "eu.timepit"            %% "refined-cats"    % refinedVersion,
      "com.beachape"          %% "enumeratum"      % enumVersion,

      // test
      "org.scalameta"       %% "munit"                         % munitVersion               % Test,
      "org.typelevel"       %% "munit-cats-effect"             % munitCatsEffectVersion     % Test,
      "com.alejandrohdezma" %% "http4s-munit"                  % http4sMunitVersion         % Test,
      "org.typelevel"       %% "log4cats-noop"                 % log4catsVersion            % Test,
      "org.typelevel"       %% "cats-effect-testing-scalatest" % scalaTestCatsEffectVersion % Test,
      "ch.qos.logback"       % "logback-classic"               % logbackVersion             % Test
    ),
    Compile / mainClass := Some("com.tiff413.livedemo.Application")
  )
  .dependsOn(core.jvm)
