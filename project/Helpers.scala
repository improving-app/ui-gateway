import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker
import sbt.{Project, Test, Tests, ThisBuild, _}
import sbt.Keys._
import kalix.sbt.KalixPlugin
import org.scalafmt.sbt.ScalafmtPlugin
import sbt.nio.Keys.{ReloadOnSourceChanges, onChangedBuildSource}
import sbtdynver.DynVerPlugin.autoImport.{dynverSeparator, dynverVTagPrefix}

// C for Configuration functions
object C {

  val scala3Options: Seq[String] = Seq(
    "-target:11",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlog-reflective-calls",
    "-Xlint"
  )

  val javaOptions: Seq[String] = Seq(
    "-Xlint:unchecked",
    "-Xlint:deprecation",
    "-parameters" // for Jackson
  )
  def kalix(artifactName: String)(project: Project): Project = {
    project
      .enablePlugins(
        KalixPlugin,
        JavaAppPackaging,
        DockerPlugin,
        ScalafmtPlugin
      )
      .settings(
        name := artifactName,
        organization := "com.improving",
        organizationHomepage := Some(url("https://improving.app")),
        licenses := Seq(
          ("Apache 2", url("https://www.apache.org/licenses/LICENSE-2.0"))
        ),
        Docker / maintainer := "reid.spencer@improving.com",
        scalaVersion := "2.13.8",
        scalacOptions := scala3Options,
        Compile / scalacOptions ++= scala3Options,
        Compile / javacOptions ++= javaOptions,
        Test / parallelExecution := false,
        Test / testOptions += Tests.Argument("-oDF"),
        Test / logBuffered := false,
        Compile / run := {
          // needed for the proxy to access the user function on all platforms
          sys.props += "kalix.user-function-interface" -> "0.0.0.0"
          (Compile / run).evaluated
        },
        run / fork := true,
        Global / cancelable := false, // ctrl-c
        Global / onChangedBuildSource := ReloadOnSourceChanges,
        ThisBuild / dynverSeparator := "-",
        ThisBuild / dynverVTagPrefix := false,
        ThisBuild / versionScheme := Some("semver-spec"),
        libraryDependencies ++= Seq(
          "org.scalatest" %% "scalatest" % "3.2.12" % Test
        ),
        dockerBaseImage := "docker.io/library/adoptopenjdk:11-jre-hotspot",
        dockerUsername := None,
        dockerRepository := Some(
          "index.docker.io/v1/weinyopp"
        ),
        dockerExposedPorts += 8080,
        dockerUpdateLatest := true,
        dockerBuildCommand := {
          if (sys.props("os.arch") != "amd64") {
            // use buildx with platform to build supported amd64 images on
            // other CPU architectures this may require that you have first
            // run 'docker buildx create' to set docker buildx up
            dockerExecCommand.value ++ Seq(
              "buildx",
              "build",
              "--platform=linux/amd64",
              "--load"
            ) ++ dockerBuildOptions.value :+ "."
          } else dockerBuildCommand.value
        }
      )
  }
}
