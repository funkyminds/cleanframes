organization := "io.funkyminds"

name := "clean-frames"

publishMavenStyle := true

version := "0.1.0-SNAPSHOT"

sparkVersion := "2.4.0"

sparkComponents := Seq("core", "sql", "hive")

scalaVersion := {
  if (sparkVersion.value >= "2.0.0") {
    "2.11.11"
  } else {
    "2.10.6"
  }
}

crossScalaVersions := {
  if (sparkVersion.value >= "2.3.0") {
    Seq("2.11.11")
  } else {
    Seq("2.10.6", "2.11.11")
  }
}

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
}

parallelExecution in Test := false
fork := true

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-language:higherKinds",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:implicitConversions"
)

libraryDependencies ++= {
  Seq(
    "com.holdenkarau" %% "spark-testing-base" % s"${sparkVersion.value}_0.11.0" % "test",
    "com.chuusai" %% "shapeless" % "2.3.3",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  )
}
