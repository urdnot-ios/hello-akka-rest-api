#!/bin/zsh

# did you change the version number?
sbt clean
sbt assembly
sbt docker:publishLocal
docker image tag iotkafkaoutsidebme680:latest intel-server-03:5000/iotkafkaoutsidebme680
docker image push intel-server-03:5000/iotkafkaoutsidebme680

# Server side:
# kubectl apply -f /home/appuser/deployments/bme680Reader.yaml
# If needed:
# kubectl delete deployment iot-bme680-kafka-reader
# For troubleshooting
# kubectl exec --stdin --tty iot-bme680-kafka-reader -- /bin/bash
