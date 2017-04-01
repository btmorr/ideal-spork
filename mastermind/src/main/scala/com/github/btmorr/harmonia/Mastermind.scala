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

import models._

/* This doesn't currently do any real analysis--it just reads from the topic, gets the
 * length of messages, and stores this in Cassandra with the message's UUID as the key.
 */
object Mastermind {
  case class Message(message: String, response: String) {
    val id = UUID.randomUUID()
  }

  def main(args: Array[String]) = {
    import AppConfig.{ CassandraConfig, KafkaConfig, SparkConfig }


    val ssc = getExectutionContext(SparkConfig.appName, SparkConfig.master, CassandraConfig.hostUri)

    /* Modify this to put the message through a dag of models, possibly including human
     * interfaces. At each point in the process, persist the stage input and output, along
     * with meta-data about the model (id tag + version). The dag should include selective
     * faculties along the way and/or at the end to choose from amongst many possible
     * responses, and ultimately choose what to reply to the interlocutor.
     *
     * What I want is to be able to define a dag of models such that for each vertex, the collected
     * outputs of its parents are a superset of its inputs, and ensure that the whole graph is
     * a) acyclic, and b) collects to a single terminal vertex. Whenever a vertex returns, the
     * graph runner should determine which un-started verticies are now available for execution
     * and start them. All verticies should execute asychronously.
     *
     * I'm sure there's a way to force the type system to guarantee this, but the first version will
     * probably have something to the effect of a GraphValidator to check these properties. I also
     * appear to have just described Apache Apex almost exactly, but that seems like overkill for
     * this toy...
     */
    val stream = getInputStream(ssc, KafkaConfig.topics)
    val msgs = stream.map( _.value )

    // This pipe is currently dead-end at the dummy vw model
    val sentences = msgs.flatMap( in => models.SentenceSegmenter( in ) )
    val lemmaTagPairs = sentences.flatMap( s => LemmatizerTagger( s ) )
    val vwPredictions = lemmaTagPairs.map{ case (word, tag) => (word, SearnPredictor( (word, tag) ) ) }

    val simpleResponses = msgs.map( in => (in, SimpleLookup( in ) ) )
    val responses = simpleResponses.map{
      case (in, resp) => Message( in, SayIt( resp ) )
    }
    responses.saveToCassandra(CassandraConfig.keyspace, CassandraConfig.table, SomeColumns("id", "message", "response"))

    ssc.start()
    ssc.awaitTermination()
  }


  private def getExectutionContext(appName: String, sparkMaster: String, cassandraUri: String) = {

    val conf = new SparkConf().
      setAppName(appName).
      setMaster(sparkMaster).
      set("spark.cassandra.connection.host", cassandraUri)

    new StreamingContext(conf, Seconds(1))
  }

  // have tried to make this polymorphic over deserializer type, but having trouble
  // getting the right type for the fields
  private def getInputStream(ssc: StreamingContext, topics: Array[String]) = {

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
}
