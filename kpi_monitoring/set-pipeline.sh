#!/bin/bash
set +e
fly --target concourse set-pipeline -p platform-monitoring --config pipeline.yml \
    --load-vars-from <(lpass show --notes "Shared-apm/concourse/credentials.yml") \
    --load-vars-from <(lpass show --notes "Shared-apm/concourse/kpi-validator.yml")