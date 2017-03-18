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
object Chat extends ServerApp {

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
    println(s"Got input: $msg")
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
  /* Ideas for app side:
   * - add login and user session components, and feature flags per user. Similarly, but a little 
   *   different, if the AI is just another chatter, then could make the feature flags a part of
   *   an AI identity instead of a user profile, such that the user interacts with a particular
   *   agent, and that agent uses particular models/features, so that the experience would vary  
   *   by which AI was participating in the convo, rather than by which human. This could make for
   *   a less-coupled system (the chat program tracking which users are logged in, and the AI engine
   *   keeping track of which agent uses which models), and also easier comparison of the behaviors of
   *   two agents, or two versions of an agent, as an agent would usually just be two unique mixtures
   *   of models (including version changes).
   * - look into scala.js or other frameworks to serve up content while keeping this predominantly scala
   * - learn how to have the content auto-refresh when content comes back from the AI side (AI as another
   *   user chatting with the human, rather than fulfilling a backend request?)--should be able to follow
   *   a simple webchat tutorial.
   */

}
