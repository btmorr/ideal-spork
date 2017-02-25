# ideal-spork
A toy, starting with Spark Streaming + Kafka (the repo name was randomly generated)

Intial start based on info gleaned from:

- [ampcamp exercise](http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html)
- [general Spark Streaming guide](https://spark.apache.org/docs/latest/streaming-programming-guide.html)
- [Spark Streaming Kafka Integration guide](https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html)

The above is sufficient for the general kinds of operations available within the Spark ecosystem, including [GraphX](https://spark.apache.org/graphx/) ops and whatnot. One really interesting tie-in would be using neural networks within this context though. See:

- [TensorFlow on Spark](http://yahoohadoop.tumblr.com/post/157196317141/open-sourcing-tensorflowonspark-distributed-deep)
- in conjunction with [Debo Datta's blog](http://debajyotidatta.github.io/nlp/deep/learning/word-embeddings/2016/11/27/Understanding-Convolutions-In-Text/)

Example of a larger-scale kafka deploy setup from [clairvoyansoft's blog](http://site.clairvoyantsoft.com/kafka-great-choice-large-scale-event-processing/)

On Cassandra:

- [Designing indicies](http://outworkers.com/blog/post/a-series-on-cassandra-part-1-getting-rid-of-the-sql-mentality) [hint: cass is more of a k-v store even than mongo--you can't query by data, must be by index]
- [Using Cassandra with Spark Streaming](https://github.com/datastax/spark-cassandra-connector/blob/master/doc/8_streaming.md)

## Developing

To get started, you have to have [Kafka](https://kafka.apache.org/downloads) (version >= 0.10.0.0), [Spark](http://spark.apache.org/downloads) (version >= 2.1.0), and [Cassandra](https://cassandra.apache.org/download/) installed, with the $SPARK_HOME and $KAFKA_HOME environment variables set. If you have not launched Zookeeper + Spark + Kafka and configured them, you can do so by launching `scripts/start-services.sh`. The processing application is launched by running `sbt run`. Then, you can start a producer terminal by launching `scripts/producer-terminal.sh`, and then typing messages. These will be streamed into the app and processed. Also, remember to start Cassandra.

In its current state, the app has been test-driven with:

- Kafka 0.10.2.0
- Spark 2.1.0
- Cassandra 3.10

### Setting up Cassandra

```
cqlsh> CREATE KEYSPACE IF NOT EXISTS test WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
cqlsh> CREATE TABLE IF NOT EXISTS test.messages(id UUID, message TEXT, len INT, PRIMARY KEY(id));
cqlsh>
```

After sending some messages through, view them by:

```
cqlsh> select * from test.messages
```
