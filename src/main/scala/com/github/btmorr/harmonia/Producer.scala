package com.github.btmorr.harmonia

import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }

/* The Producer just sends some preset messages through a Kafka topic. This is currently
 * fairly trivial, but proves that it can connect to the server and send the data. This
 * can be later modified to read from any kind of real-time source, such as a web-page
 * with a chat window, a Twitter account, an email inbox, command-line args, etc.
 */
object Producer extends App {
  def initializeProducer(zkServer: String): KafkaProducer[String, String] = {
    import java.util.Properties

    val props = new Properties()
    props.put("bootstrap.servers", zkServer)
    props.put("acks", "all")
    props.put("retries", "0")
    props.put("batch.size", "16384")
    props.put("linger.ms", "1")
    props.put("buffer.memory", "33554432")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    new KafkaProducer[String, String](props)
  }

  implicit val stringToProducerRecord: String => ProducerRecord[String, String] = msg => new ProducerRecord[String, String]("test", msg)

  val producer = initializeProducer("localhost:9092")

  producer.send("thisThing")
  producer.send("thatOtherThing")
  //not working yet
}
