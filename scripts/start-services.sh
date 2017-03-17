#!/usr/bin/env bash

echo "Starting Zookeeper server with default properties"
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > /dev/null &

# Hacky, but the zookeeper process has to have a chance to come up, and doesn't return, might have to turn
# this into a Python or Scala script that tails the output and looks for the "Up" message
sleep 5

echo "Starting Kafka server with default properties"
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties > /dev/null &

# Same as above. Good enough for now.
sleep 5

echo "Starting Cassandra"
cassandra > /dev/null &

#$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
