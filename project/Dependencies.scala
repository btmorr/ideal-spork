import sbt._

object Dependencies {

  object http4s {
    val http4sVersion = "0.15.7a"
    lazy val blazeServer = "org.http4s"         %% "http4s-blaze-server" % http4sVersion
    lazy val blazeClient = "org.http4s"         %% "http4s-blaze-client" % http4sVersion
    lazy val dsl = "org.http4s"         %% "http4s-dsl" % http4sVersion
    lazy val argonaut = "org.http4s"         %% "http4s-argonaut" % http4sVersion
    lazy val slf4j = "org.slf4j"           % "slf4j-simple" % "1.6.4"
    lazy val all = Seq( blazeServer, blazeClient, dsl, argonaut, slf4j )
  }


  object apache {
    val sparkVersion = "2.1.0"
    lazy val kafkaClients = "org.apache.kafka"    % "kafka-clients" % "0.10.0.1"
    lazy val sparkCassandra = "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-RC1"
    lazy val sparkSql = "org.apache.spark"   %% "spark-sql" % sparkVersion
    lazy val sparkStreaming = "org.apache.spark"   %% "spark-streaming" % sparkVersion
    lazy val sparkKafka = "org.apache.spark"   %% "spark-streaming-kafka-0-10" % sparkVersion
    lazy val all = Seq( kafkaClients, sparkCassandra, sparkSql, sparkStreaming, sparkKafka )
  }

  object nlp {
    lazy val stanfordNLP = "edu.stanford.nlp"    % "stanford-corenlp" % "3.7.0"
    lazy val all = Seq( stanfordNLP )
  }

  object test {
    val scalatestVersion = "3.0.1"
    lazy val scalatest = "org.scalatest"      %% "scalatest" % scalatestVersion % "test"
    lazy val scalactic = "org.scalactic"      %% "scalactic" % scalatestVersion
    lazy val all = Seq( scalatest, scalactic )
  }
}
