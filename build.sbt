Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / dynverSeparator := "-"

lazy val akkaClient = project
  .in(file("akka-client"))
  .configure(C.basic)
  .enablePlugins(AkkaGrpcPlugin)
  .dependsOn(gateway, gateway % "protobuf")

lazy val gateway = project
  .in(file("gateway"))
  .configure(C.kalix("improving-app-gateway"))
  .settings(
    Compile / unmanagedResourceDirectories += sourceDirectory.value / "main" / "proto"
  )

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"
