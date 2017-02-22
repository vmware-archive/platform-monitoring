#!/usr/bin/env bash

cf api  https://api.sys.fendi.gcp.pcf-metrics.com --skip-ssl-validation
cf login -u admin -p SL-mzOK8qVO1iNgvuqWtu2WrfyWQyX-2 -o system -s system

cf create-quota QUOTA -m 50GB -i -1 || true

cf push test-app -p assets/test-app.jar -m 50GB
cf delete test-app -f
cf delete-orphaned-routes -f

