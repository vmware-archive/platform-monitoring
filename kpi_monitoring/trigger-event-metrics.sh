#!/usr/bin/env bash

cf api  $CF_API --skip-ssl-validation
cf login -u $CF_USERNAME -p $CF_PASSWORD -o system -s system

cf create-quota QUOTA -m 50GB -i -1 || true

cf push test-app -p assets/test-app.jar -m 50GB
cf delete test-app -f
cf delete-orphaned-routes -f

