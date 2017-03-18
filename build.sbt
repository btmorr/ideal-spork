import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "com.github.btmorr",

  version in ThisBuild := "0.1." + {
    val gitHeadCommitSha = "git rev-parse --short HEAD".!!.trim
    sys.env.getOrElse("CIRCLE_BUILD_NUM", s"$gitHeadCommitSha-SNAPSHOT")
  },

  scalaVersion := "2.11.8",

  libraryDependencies ++= Dependencies.test.all
)

lazy val chat = (project in file("chat")).
  settings(
    commonSettings,
    name := "harmonia-chat",
    libraryDependencies ++= Dependencies.http4s.all,
    libraryDependencies += Dependencies.apache.kafkaClients
  )

lazy val mastermind = (project in file("mastermind")).
  settings(
    commonSettings,
    name := "harmonia-mastermind",
    libraryDependencies ++= Dependencies.apache.all,
    libraryDependencies ++= Dependencies.nlp.all
  )
