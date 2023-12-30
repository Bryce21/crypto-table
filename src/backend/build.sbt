name := "backend"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies ++=
  Seq(
    "dev.zio" %% "zio-http" % "3.0.0-RC4",
    "dev.zio" %% "zio-json" % "0.6.2",
    "dev.zio" %% "zio-test" % "2.0.15" % Test,
    "dev.zio" %% "zio-json" % "0.6.2",
    "dev.zio" %% "zio-logging" % "2.1.16",
    "dev.zio" %% "zio-kafka" % "2.7.1",
    "dev.zio" %% "zio-kafka-testkit" % "2.7.1" % Test,
    "com.typesafe" % "config" % "1.4.3",
  )
