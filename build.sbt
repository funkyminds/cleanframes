lazy val sparkVersion = "2.4.0"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "Clean Frames",
    version := "0.1.0-SNAPSHOT",
    organization := "io.funkyminds",
    scalaVersion := "2.11.12",
    crossScalaVersions := Seq("2.11.12", "2.12.8"),
    scalacOptions ++= Seq(
      "-target:jvm-1.8",
      "-Xfatal-warnings",
      "-language:higherKinds",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:implicitConversions"
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
      "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
      "com.holdenkarau" %% "spark-testing-base" % {sparkVersion + "_0.11.0"} % Test,
      "org.apache.spark" %% "spark-hive" % sparkVersion % Test,
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test,it"
    ),
    parallelExecution in Test := false,
    fork := true
  )
