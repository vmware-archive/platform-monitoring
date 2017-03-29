#!/usr/bin/env bash
set -ex

gradle clean
gradle build

java -DHOSTNAME=$PROVIDER_IP \
     -DUSERNAME=$PROVIDER_USERNAME \
     -DPASSWORD=$PROVIDER_PASSWORD \
     -DCF_DEPLOYMENT_NAME=cf \
     -DNOZZLE_PREFIX=opentsdb.nozzle. \
     -DRUN_TIME_MINUTES=$RUN_TIME_MINUTES \
     -DPOLL_INTERVAL_SECONDS=5 \
     -jar build/libs/platform-monitoring-validator-1.1-jar-with-dependencies.jar