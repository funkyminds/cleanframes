lazy val sparkVersion = "2.4.0"

lazy val projectVersion = "2.1.0_0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "clean-frames",
    version := projectVersion,
    organization := "io.funkyminds",
    scalaVersion := "2.11.12",
    crossScalaVersions := {
      if (sparkVersion >= "2.3.0") {
        Seq("2.11.12")
      } else {
        Seq("2.10.6", "2.11.12")
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
      val ver = if (sparkVersion >= "2.1.1") "1.8" else "1.7"

      Seq(
        "-source", ver,
        "-target", ver,
        "-Xms2G",
        "-Xmx2G",
        "-XX:MaxPermSize=2048M",
        "-XX:+CMSClassUnloadingEnabled"
      )
    },
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
      "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
      "org.apache.spark" %% "spark-hive" % sparkVersion % Test,
      "com.holdenkarau" %% "spark-testing-base" % {sparkVersion + "_0.11.0"} % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % "test,it"
    ),
    parallelExecution in Test := false,
    fork := true,
    sparkComponents := Seq("core", "sql", "hive"),
    publishMavenStyle := true
  )