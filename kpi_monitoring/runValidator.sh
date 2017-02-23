#!/usr/bin/env bash
mvn clean
mvn package


java -DHOSTNAME=$PROVIDER_IP \
     -DUSERNAME=$PROVIDER_USERNAME \
     -DPASSWORD=$PROVIDER_PASSWORD \
     -DCF_DEPLOYMENT_NAME=cf \
     -DNOZZLE_PREFIX=opentsdb.nozzle. \
     -DRUN_TIME_MINUTES=5 \
     -DPOLL_INTERVAL_SECONDS=5 \
    -jar target/platform-monitoring-validator-1.1-jar-with-dependencies.jar