import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker
import kalix.sbt.KalixPlugin
import sbt.Keys._
import sbt.nio.Keys.{ReloadOnSourceChanges, onChangedBuildSource}
import sbt.{Project, Test, Tests, _}
import sbtdynver.DynVerPlugin.autoImport.{dynverSeparator, dynverVTagPrefix}
import sbtprotoc.ProtocPlugin.autoImport.PB

// C for Configuration functions
object C {

  val scalaOptions = Seq(
    "-Xsource:3",
    "-Wdead-code",
    "-deprecation",
    "-feature",
    "-Werror",
    "-Wunused:imports", // Warn if an import selector is not referenced.
    "-Wunused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Wunused:privates", // Warn if a private member is unused.
    "-Wunused:locals", // Warn if a local definition is unused.
    "-Wunused:explicits", // Warn if an explicit parameter is unused.
    "-Wunused:implicits", // Warn if an implicit parameter is unused.
    "-Wunused:params", // Enable -Wunused:explicits,implicits.
    "-Xlint:nonlocal-return", // A return statement used an exception for flow control.
    "-Xlint:implicit-not-found", // Check @implicitNotFound and @implicitAmbiguous messages.
    "-Xlint:serial", // @SerialVersionUID on traits and non-serializable classes.
    "-Xlint:valpattern", // Enable pattern checks in val definitions.
    "-Xlint:eta-zero", // Warn on eta-expansion (rather than auto-application) of zero-ary method.
    "-Xlint:eta-sam", // Warn on eta-expansion to meet a Java-defined functional
    "-target:11",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlog-reflective-calls",
    "-Xlint"
  )

  val javaOptions = Seq(
    "-Xlint:unchecked",
    "-Xlint:deprecation",
    "-parameters" // for Jackson
  )

  def basic(project: Project): Project = {
    project.settings(
      Docker / maintainer := "reid.spencer@improving.com",
      organization := "com.improving",
      organizationHomepage := Some(url("https://improving.app")),
      licenses := Seq(
        ("Apache 2", url("https://www.apache.org/licenses/LICENSE-2.0"))
      ),
      scalaVersion := "2.13.8",
      scalacOptions := scalaOptions,
      Compile / javacOptions ++= javaOptions,
      Test / parallelExecution := false,
      Test / testOptions += Tests.Argument("-oDF"),
      Test / logBuffered := false,
      libraryDependencies ++= Seq(
        "org.scalactic" %% "scalactic" % "3.2.13",
        "org.scalatest" %% "scalatest" % "3.2.12" % Test
      ),
      Compile / PB.protoSources ++= Seq(
        baseDirectory.value.getParentFile / "api" / "src" / "proto"
      ),
      ThisBuild / dynverSeparator := "-",
      ThisBuild / dynverVTagPrefix := false,
      ThisBuild / versionScheme := Some("semver-spec")
    )
  }

  def kalix(artifactName: String)(project: Project): Project = {
    project
      .enablePlugins(KalixPlugin, JavaAppPackaging, DockerPlugin)
      .configure(basic)
      .settings(
        name := artifactName,
        Compile / run := {
          // needed for the proxy to access the user function on all platforms
          sys.props += "kalix.user-function-interface" -> "0.0.0.0"
          (Compile / run).evaluated
        },
        run / fork := true,
        Global / cancelable := false,
        Global / onChangedBuildSource := ReloadOnSourceChanges,
        dockerBaseImage := "docker.io/library/adoptopenjdk:11-jre-hotspot",
        dockerUsername := None,
        dockerRepository := Some(
          "us-east1-docker.pkg.dev/hardy-beach-350414/improving-app-images"
        ),
        dockerExposedPorts += 8080,
        dockerUpdateLatest := true,
        dockerBuildCommand := {
          if (sys.props("os.arch") != "amd64") {
            // use buildx with platform to build supported amd64 images on other CPU architectures
            // this may require that you have first run 'docker buildx create' to set docker buildx up
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
