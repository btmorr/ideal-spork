#!/usr/bin/env bash

$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > /dev/null &

$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties > /dev/null &

#$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
