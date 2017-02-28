package com.github.btmorr.harmonia

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.kafka.common.serialization.StringDeserializer
import com.datastax.spark.connector.SomeColumns
import com.datastax.spark.connector.streaming._
import java.util.UUID
import scala.sys.process._
import Models._

/* This doesn't currently do any real analysis--it just reads from the topic, gets the
 * length of messages, and stores this in Cassandra with the message's UUID as the key.
 */
object Consumer extends App {

  case class Message(message: String, response: String) {
    val id = UUID.randomUUID()
  }

  def getExectutionContext(dbUri: String) = {
    val appName = "harmonia"
    val master = "local[2]"

    val conf = new SparkConf().
      setAppName(appName).
      setMaster(master).
      set("spark.cassandra.connection.host", dbUri)

    new StreamingContext(conf, Seconds(1))
  }

  def getInputStream(ssc: StreamingContext, topics: Array[String]) = {

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
  }

  val cassandraHost = "127.0.0.1"
  val keyspace = "test"
  val table = "messages"

  val ssc = getExectutionContext(cassandraHost)

  val stream = getInputStream(ssc, Array("test"))
  val msgs = stream.map(record => {
    val resp = SimpleLookupModel(record.value)
    s"say $resp".!
    Message(record.value, resp)
  })
  msgs.saveToCassandra(keyspace, table, SomeColumns("id", "message", "response"))

  ssc.start()
  ssc.awaitTermination()
}
