# ideal-spork
A toy, starting with Spark Streaming + Kafka (the repo name was randomly generated). Currently, either the bundled Kafka producer terminal, or the Producer application will emit a series of messages, which are transmitted over Kafka, picked up by the Consumer, written to Cassandra, and said out loud (if you're on a Mac) with the `say` command. :p

Intial start based on info gleaned from:

- [ampcamp exercise](http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html)
- [general Spark Streaming guide](https://spark.apache.org/docs/latest/streaming-programming-guide.html)
- [Spark Streaming Kafka Integration guide](https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html)

The above is sufficient for the general kinds of operations available within the Spark ecosystem, including [GraphX](https://spark.apache.org/graphx/) ops and whatnot. One really interesting tie-in would be using neural networks within this context though. See:

- [TensorFlow on Spark](http://yahoohadoop.tumblr.com/post/157196317141/open-sourcing-tensorflowonspark-distributed-deep)
- in conjunction with [Debo Datta's blog](http://debajyotidatta.github.io/nlp/deep/learning/word-embeddings/2016/11/27/Understanding-Convolutions-In-Text/)

Example of a larger-scale kafka deploy setup from [clairvoyansoft's blog](http://site.clairvoyantsoft.com/kafka-great-choice-large-scale-event-processing/)

On Cassandra:

- [Designing indicies](http://outworkers.com/blog/post/a-series-on-cassandra-part-1-getting-rid-of-the-sql-mentality)
- [Using Cassandra with Spark Streaming](https://github.com/datastax/spark-cassandra-connector/blob/master/doc/8_streaming.md)

## Developing

To get started, you have to have [Kafka](https://kafka.apache.org/downloads) (version >= 0.10.0.0), [Spark](http://spark.apache.org/downloads) (version >= 2.1.0), and [Cassandra](https://cassandra.apache.org/download/) installed (preferably included in the PATH), with the $SPARK_HOME and $KAFKA_HOME environment variables set. Instructions below assume you have these variables correclty configured.

In its current state, the app has been test-driven with:

- Kafka 0.10.2.0
- Spark 2.1.0
- Cassandra 3.10

## Setting up the stack

You must have Zookeeper, Kafka, and Cassandra up to run the applications. Spark does not have to be running to execute the applications using `sbt run`, but having a cluster up will allow you to submit assembled jars as you would in a production environment using `spark-submit`. You can shortcut individual setup of the required services by launching `scripts/start-services.sh`.

### Launching Zookeeper

Kafka requires a Zookeeper cluster. It comes bundled with Zookeeper and a starter config, so you can test locally without having to put together a proper cluster. Run:

```
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
```

### Launching Kafka

Once Zookeeper is up, launch Kafka:

```
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties
```

To set up a topic (only has to be done once per topic):

```
$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

### Launching Cassandra

If Cassandra's 'bin' directory is included on your PATH, you can simply run `cassandra` to start the server (the terminal process will return, but the server will still be up. You can verify this and interact with the database by opening the CQL shell: `cqlsh`.

Before reading/writing, you have to set up the keyspace and table (this only has to be done once, as long as the keyspace and table don't change):

```
cqlsh> CREATE KEYSPACE IF NOT EXISTS test WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
cqlsh> CREATE TABLE IF NOT EXISTS test.messages(id UUID, message TEXT, len INT, PRIMARY KEY(id));
cqlsh>
```

After sending some messages through, view them by:

```
cqlsh> select * from test.messages
```

## Running the application

Start the Consumer and the Producer, then navigate to [localhost:8080](http://localhost:8080). The root page won't display anything at the moment, but you can hit the "send" route and add body text under the [`msg` parameter](http://localhost:8080/send?msg=hello-world) to send a message along and have the computer say it.
