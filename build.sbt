organization := "com.github.btmorr"

name := "harmonia"

version := "0.1.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka"    % "kafka-clients" % "0.10.0.1",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-RC1",
  "org.apache.spark"   %% "spark-sql" % "2.1.0",
  "org.apache.spark"   %% "spark-streaming" % "2.1.0",
  "org.apache.spark"   %% "spark-streaming-kafka-0-10" % "2.1.0"
)
