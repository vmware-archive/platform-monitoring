#!/bin/bash
set +e

lpass ls > /dev/null # check that we're logged in

fly --target concourse set-pipeline -p platform-monitoring --config pipeline.yml \
    --load-vars-from <(lpass show --notes "Shared-apm/concourse/credentials.yml") \
    --load-vars-from <(lpass show --notes "Shared-apm/concourse/kpi-validator.yml")