Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / dynverSeparator := "-"

lazy val akkaClient = project
  .in(file("akka-client"))
  .configure(C.basic)
  .enablePlugins(AkkaGrpcPlugin)
  .dependsOn(kalixService, kalixService % "protobuf")

lazy val kalixService = project
  .in(file("kalix-service"))
  .configure(C.kalix("kalix-service"))
  .settings(
    Compile / unmanagedResourceDirectories += sourceDirectory.value / "main" / "proto"
  )

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"
