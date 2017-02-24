# ideal-spork
A toy, starting with Spark Streaming + Kafka

Intial start based on info gleaned from:

- [ampcamp exercise](http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html)
- [general Spark Streaming guide](https://spark.apache.org/docs/latest/streaming-programming-guide.html)
- [Spark Streaming Kafka Integration guide](https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html)

The above is sufficient for the general kinds of operations available within the Spark ecosystem, including [GraphX](https://spark.apache.org/graphx/) ops and whatnot. One really interesting tie-in would be using neural networks within this context though. See:

- [TensorFlow on Spark](http://yahoohadoop.tumblr.com/post/157196317141/open-sourcing-tensorflowonspark-distributed-deep)
- in conjunction with [Debo Datta's blog](http://debajyotidatta.github.io/nlp/deep/learning/word-embeddings/2016/11/27/Understanding-Convolutions-In-Text/)


## Developing

To get started, you have to have [Kafka](https://kafka.apache.org/downloads) (version >= 0.10.0.0) and [Spark](http://spark.apache.org/downloads) (version >= 2.1.0) installed, with the $SPARK_HOME and $KAFKA_HOME environment variables set. If you have not launched Zookeeper + Spark + Kafka and configured them, you can do so by launching `scripts/start-services.sh`. The processing application is launched by running `sbt run`. Then, you can start a producer terminal by launching `scripts/producer-terminal.sh`, and then typing messages. These will be streamed into the app and processed.

In its current state, the app has been test-driven with:

- Kafka 0.10.2.0
- Spark 2.1.0
