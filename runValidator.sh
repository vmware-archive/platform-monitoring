#!/usr/bin/env bash
set -ex

java \
     -DCF_DEPLOYMENT_NAME=${CF_DEPLOYMENT_NAME} \
     -DCF_API=${CF_API} \
     -DCF_USERNAME=${CF_ADMIN_USERNAME} \
     -DCF_PASSWORD=${CF_ADMIN_PASSWORD} \
     -DRUN_TIME_MINUTES=${RUN_TIME_MINUTES} \
     -jar build/libs/platform-monitoring-validator-1.1-jar-with-dependencies.jar