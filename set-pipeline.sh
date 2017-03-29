#!/bin/bash
set +e

if [ -z "$1" ]
  then
    echo "usage ./set-pipeline cf | opsman | kpi-validator"
    exit
fi

lpass ls > /dev/null # check that we're logged in

fly --target concourse set-pipeline -p kpi-validator --config pipeline-$1.yml \
    --load-vars-from <(lpass show --notes "Shared-apm/concourse/credentials.yml") \
    --load-vars-from <(lpass show --notes "Shared-apm/concourse/kpi-validator.yml")