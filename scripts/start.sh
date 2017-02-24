#!/usr/bin/env bash

$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > /dev/null &

$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties > /dev/null &

$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

echo "Starting Kafka producer console. Type messages and hit 'Enter' to send. Enter Ctl-C to end:"
$KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

#To see the output:
#$KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
