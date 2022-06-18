
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / dynverSeparator := "-"

lazy val gateway = project
  .in(file("."))
  .configure(C.kalix("improving-ui-gateway"))
