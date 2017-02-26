organization := "com.github.btmorr"

name := "harmonia"

version in ThisBuild := "0.1." + {
  val gitHeadCommitSha = "git rev-parse --short HEAD".!!.trim
  sys.env.getOrElse("CIRCLE_BUILD_NUM", s"$gitHeadCommitSha-SNAPSHOT")
}

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.slf4j"           % "slf4j-simple" % "1.6.4",
  "org.http4s"         %% "http4s-blaze-server" % "0.15.5",
  "org.http4s"         %% "http4s-blaze-client" % "0.15.5",
  "org.http4s"         %% "http4s-dsl" % "0.15.5",
  "org.http4s"         %% "http4s-argonaut" % "0.15.5",
  "org.apache.kafka"    % "kafka-clients" % "0.10.0.1",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-RC1",
  "org.apache.spark"   %% "spark-sql" % "2.1.0",
  "org.apache.spark"   %% "spark-streaming" % "2.1.0",
  "org.apache.spark"   %% "spark-streaming-kafka-0-10" % "2.1.0",
  "org.scalatest"      %% "scalatest" % "3.0.0" % "test"
)
