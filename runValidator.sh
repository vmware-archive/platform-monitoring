#!/usr/bin/env bash
set -ex

java \
     -DCF_DEPLOYMENT_NAME=cf \
     -DCF_API=api.$SYS_DOMAIN \
     -DCF_USERNAME=$CF_USERNAME \
     -DCF_PASSWORD=$CF_PASSWORD \
     -DRUN_TIME_MINUTES=$RUN_TIME_MINUTES \
     -jar build/libs/platform-monitoring-validator-1.1-jar-with-dependencies.jar