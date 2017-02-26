package com.github.btmorr.harmonia

import org.http4s._
import org.http4s.dsl._
import org.http4s.server.{ Server, ServerApp }
import org.http4s.server.blaze._
import scalaz.concurrent.Task
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }

/* The Producer just sends some preset messages through a Kafka topic. This is currently
 * fairly trivial, but proves that it can connect to the server and send the data. This
 * can be later modified to read from any kind of real-time source, such as a web-page
 * with a chat window, a Twitter account, an email inbox, command-line args, etc.
 */
object Producer extends ServerApp {

  implicit val S = scalaz.concurrent.Strategy.DefaultTimeoutScheduler

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

  def sendMessage(msg: String) = {
    val resp = producer.send(msg).get()
    s"Sending message: $msg - Response: $resp"
  }

  object MsgParam extends QueryParamDecoderMatcher[String]("msg")

  val helloWorldService = HttpService {
    case GET -> Root / "ping" =>
      Ok("pong")

    case GET -> Root / "send" :? MsgParam(message) =>
      Ok( sendMessage( message ) )
  }

  val bindPort = sys.env.getOrElse( "PORT", "8080" ).toInt
  val builder = BlazeBuilder.bindHttp( bindPort, "0.0.0.0" ).mountService( helloWorldService, "/" )

  override def server( args: List[String] ): Task[Server] = builder.start

}
