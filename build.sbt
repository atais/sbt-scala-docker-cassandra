import sbt.Keys.{scalaVersion, version}

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.6"
)

lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % Test

lazy val client = (project in file("client"))
  .settings(
    commonSettings,
    name := "client",
    libraryDependencies ++= Seq(
      "cassandra-driver-core",
      "cassandra-driver-mapping"
    ).map("com.datastax.cassandra" % _ % "3.5.0")
  )

lazy val testcontainers = (project in file("testcontainers"))
  .settings(
    commonSettings,
    name := "testcontainers",
    libraryDependencies ++= Seq(
      scalatest,
      "com.dimafeng" %% "testcontainers-scala" % "0.17.0" % Test,
    )
  )
  .dependsOn(
    client % Test
  )

lazy val whisk = (project in file("whisk"))
  .settings(
    commonSettings,
    name := "whisk",
    libraryDependencies ++= Seq(
      scalatest,
      "com.whisk" %% "docker-testkit-scalatest" % "0.9.5" % Test,
      "com.whisk" %% "docker-testkit-impl-docker-java" % "0.9.5" % Test,
    )
  )
  .dependsOn(
    client % Test
  )

lazy val root = (project in file("."))
  .aggregate(
    client,
    testcontainers,
    whisk
  )