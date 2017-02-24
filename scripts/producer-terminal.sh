#!/usr/bin/env bash

echo "Starting Kafka producer console. Type messages and hit 'Enter' to send. Enter Ctl-C to end:"
$KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

#To see the output:
#$KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
