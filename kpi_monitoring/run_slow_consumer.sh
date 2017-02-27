#!/usr/bin/env bash

cf api  $CF_API --skip-ssl-validation
cf login -u $CF_USERNAME -p $CF_PASSWORD -o system -s system

export DOPPLER_ADDR=wss://doppler.sys.prada.gcp.pcf-metrics.com:443
export CF_ACCESS_TOKEN=$(cf oauth-token)

./assets/slow_consumer