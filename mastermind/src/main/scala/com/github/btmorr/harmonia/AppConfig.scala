package com.github.btmorr.harmonia

object AppConfig {

  object CassandraConfig {
    val hostUri = "127.0.0.1"
    val keyspace = "test"
    val table = "messages"
  }

  object KafkaConfig {
    val zookeeperUri = "localhost:9092"
    val topics: Array[String] = Array("test")
  }

  object SparkConfig {
    val appName = "harmonia"
    val master = "local[2]"
  }
}
