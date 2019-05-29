val sparkVersion = settingKey[String]("Spark version")

val cleanframesVersion = settingKey[String]("cleanframes version without Spark version part")

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "cleanframes",
    publishSettings,
    publishMavenStyle := true,
    sparkVersion := System.getProperty("sparkVersion", "2.4.0"),
    cleanframesVersion := "0.3.0-SNAPHOST",
    version := sparkVersion.value + "_" + cleanframesVersion.value,
    organization := "io.funkyminds",
    scalaVersion := "2.11.12",
    crossScalaVersions := {
      if (sparkVersion.value >= "2.4.0") {
        Seq("2.11.11", "2.12.7")
      } else if (sparkVersion.value >= "2.3.0") {
        Seq("2.11.11")
      } else {
        Seq("2.10.6", "2.11.11")
      }
    },
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-language:higherKinds,implicitConversions",
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    javacOptions ++= {
      val ver = if (sparkVersion.value >= "2.1.1") "1.8" else "1.7"

      Seq(
        "-source", ver,
        "-target", ver,
        "-Xms2G",
        "-Xmx2G",
        "-XX:MaxPermSize=2048M",
        "-XX:+CMSClassUnloadingEnabled"
      )
    },
    javaOptions ++= Seq("-Xms2G", "-Xmx2G", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      // @formatter:off
      "com.chuusai"       %% "shapeless"            % "2.3.3",
      "org.apache.spark"  %% "spark-core"           % sparkVersion.value                % Provided,
      "org.apache.spark"  %% "spark-sql"            % sparkVersion.value                % Provided,
      "org.apache.spark"  %% "spark-hive"           % sparkVersion.value                % Provided,
      "com.holdenkarau"   %% "spark-testing-base"   % {sparkVersion.value + "_0.12.0"}  % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"                           % "test,it"
      // @formatter:om
    ),
    parallelExecution in Test := false,
    fork := true,
    publishMavenStyle := true
  )

lazy val publishSettings = Seq(
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },

  licenses := Seq("Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),

  homepage := Some(url("https://github.com/funkyminds/cleanframes")),

  scmInfo := Some(ScmInfo(
    url("https://github.com/funkyminds/cleanframes.git"),
    "scm:git@github.com:funkyminds/cleanframes.git"
  )),

  developers := List(
    Developer("dawrut", "Dawid Rutowicz", "", url("https://github.com/funkyminds/"))
  ),

  //credentials += Credentials(Path.userHome / ".ivy2" / ".spcredentials")
  credentials ++= Seq(Credentials(Path.userHome / ".ivy2" / ".sbtcredentials"), Credentials(Path.userHome / ".ivy2" / ".sparkcredentials")),
  useGpg := true
)