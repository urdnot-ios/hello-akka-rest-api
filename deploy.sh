#!/bin/zsh

# did you change the version number?
sbt clean
sbt assembly
sbt docker:publishLocal
docker image tag basicrestapi:latest intel-server-03:5000/basicrestapi
docker image push intel-server-03:5000/basicrestapi
rsync basicrestapi.yaml appuser@intel-server-01:/home/appuser/deployments/

# Server side:
# kubectl apply -f /home/appuser/deployments/basicrestapi.yaml
# If needed:
# kubectl delete deployment iot-bme680-kafka-reader
# For troubleshooting
# kubectl exec --stdin --tty basic-rest-api-d5f4d4589-wtjkr -- /bin/bash
