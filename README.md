# ideal-spork
A toy, starting with Spark Streaming + Kafka (the repo name was randomly generated). Currently, either the bundled Kafka producer terminal, or the Producer application will emit a series of messages, which are transmitted over Kafka, picked up by the Consumer, written to Cassandra, and said out loud (if you're on a Mac) with the `say` command. :p

## Getting Started

### Building and booting the system

The only native requirement for running the application is [Vagrant](https://www.vagrantup.com/docs/installation/). Once you have Vagrant installed, add the host manager (takes care of networking between Vagrant boxes):
 
```
$ vagrant plugin install vagrant-hostmanager
```

Then navigate in the terminal to where you have this repository checked out, and:

```
$ vagrant up
```

This causes a number of things to happen. 

1. Vagrant brings up a command box that is used to provision other boxes via Ansible playbooks. 
1. Other boxes are created for the [ZooKeeper](http://zookeeper.apache.org/), [Kafka](https://kafka.apache.org/), [Spark](http://spark.apache.org/), and [Cassandra](https://cassandra.apache.org/) nodes (currently only one of each type--this could definetly be improved by modifying the configs and adding nodes to make this actually distributed).
1. Boxes are created for the apps from this project: a chat server in which users can chat with AI agents, and Mastermind, in which the agents reside.
1. Ansible provisions each of those boxes accordingly, installing the specified software, configuring it, and bringing up the relevant processes.

Whenever you're done, you can free up the resources allocated with a disproportinately satisfying command:

```
$ vagrant destroy
```

### Interacting with the system

The primary means of interaction is through [the chat interface](http://chat1:8080), where you can register a user, log in, select another user to chat with, and have a conversation. Obviously, when running this locally, the only other users will be those on your local network, and therefore probably all AIs unless you share the link with other humans. The aim is to have user interactions handled the same whether a user is AI or human. 

The only other typical interaction will be to inspect Cassandra tables. To access these, do:

```
<your-terminal> $ vagrant ssh cassandra1
ubuntu@cassandra1 $ cqlsh
cqlsh> select * from test.messages
```


## Notes

Intial start based on info gleaned from:

- [ampcamp exercise](http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html)
- [general Spark Streaming guide](https://spark.apache.org/docs/latest/streaming-programming-guide.html)
- [Spark Streaming Kafka Integration guide](https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html)

The above is sufficient for the general kinds of operations available within the Spark ecosystem, including [GraphX](https://spark.apache.org/graphx/) ops and whatnot. 

### Models

The `Models` object contains interfaces for grabbing various kinds of predictions, whether those are run within the app, or accessed through API calls to external services (as with models that are tied to another language, such as Tensorflow networks)

Worth looking into:

- [Debo Datta's blog](http://debajyotidatta.github.io/nlp/deep/learning/word-embeddings/2016/11/27/Understanding-Convolutions-In-Text/)
- [Vowpal Wabbit](https://github.com/JohnLangford/vowpal_wabbit/wiki/Tutorial)
    - [Java interface](http://search.maven.org/#search|gav|1|g%3A"com.github.johnlangford"%20AND%20a%3A"vw-jni")
    - [An entity relation example](https://github.com/JohnLangford/vowpal_wabbit/tree/8.1.1/demo/entityrelation) that seems to have been removed or moved in 8.2(???) but should still provide a valid example for training/running a model
- [Edward](http://edwardlib.org)
- [General reinforcement learning resources](http://www.wildml.com/2016/10/learning-reinforcement-learning/)
- [Updated Parsey McParseface](https://research.googleblog.com/2017/03/an-upgrade-to-syntaxnet-new-models-and.html)

### Interaction

Main idea is chatting with the AI in a chatroom. The AI will be logged into a user session just like a human, and the chat application doesn't have to be aware of the AI (aside from possibly exposing whatever the AI pipe needs to know when a message has come in and read it). This means the chat app can be put together from a tutorial, largely independent of the design of the AI. Current state of the app is just an endpoint hooked up to a Kafka producer, so this concept hasn't been put into practice at all, but that's the plan.

This also could be used to capture human<->human chats as data for training the bot.

[Http4s](http://http4s.org/v0.16/entity/) bundles [twirl](https://github.com/playframework/twirl) for serving templates, so this should be able to avoid JS (or resort to Scala.js if necessary)

### Developing

To get started, you have to have the [Stanford CoreNLP models](http://stanfordnlp.github.io/CoreNLP/) downloaded to the "lib" folder.


#### Launching Zookeeper

Kafka requires a Zookeeper cluster. It comes bundled with Zookeeper and a starter config, so you can test locally without having to put together a proper cluster. Run:

```
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
```

#### Launching Kafka

Once Zookeeper is up, launch Kafka:

```
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties
```

To set up a topic (only has to be done once per topic):

```
$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

Example of a larger-scale Kafka deploy setup from [clairvoyansoft's blog](http://site.clairvoyantsoft.com/kafka-great-choice-large-scale-event-processing/)

#### Launching Cassandra

If Cassandra's 'bin' directory is included on your PATH, you can simply run `cassandra` to start the server (the terminal process will return, but the server will still be up. You can verify this and interact with the database by opening the CQL shell: `cqlsh`.

Before reading/writing, you have to set up the keyspace and table (this only has to be done once, as long as the keyspace and table don't change):

```
cqlsh> CREATE KEYSPACE IF NOT EXISTS test WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
cqlsh> CREATE TABLE IF NOT EXISTS test.messages(id UUID, message TEXT, response TEXT, PRIMARY KEY(id));
cqlsh>
```

After sending some messages through, view them by:

```
cqlsh> select * from test.messages
```

Notes on Cassandra:

- [Designing indicies](http://outworkers.com/blog/post/a-series-on-cassandra-part-1-getting-rid-of-the-sql-mentality)
- [Using Cassandra with Spark Streaming](https://github.com/datastax/spark-cassandra-connector/blob/master/doc/8_streaming.md)


### Running the application

Start the Consumer and the Producer, then navigate to [localhost:8080](http://localhost:8080). The root page won't display anything at the moment, but you can hit the "send" route and add body text under the [`msg` parameter](http://localhost:8080/send?msg=hello-world) to send a message along and have the computer say it.


## Dev Plan

Currently, everything runs natively. The next step is to take everything that is currently running, and the required infrastructure, and automate the process of provisioning, install, etc using some combination of Vagrant and Ansible.

After that, separate components will run on their own nodes, exactly as they would in an actual distributed environment. This could include running Stanford Core NLP as a service instead of using it as a library.


Copyright (c) 2017 Benjamin Morris
