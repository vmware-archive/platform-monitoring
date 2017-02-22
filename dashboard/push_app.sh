#!/usr/bin/env bash


gradle clean && gradle build
cf push platform-monitoring -p build/libs/PlatformMonitoring-0.0.1-SNAPSHOT.jar --no-start

cf set-env platform-monitoring JMX_USERNAME 'root'
cf set-env platform-monitoring JMX_PASSWORD 'root'
cf set-env platform-monitoring JMX_SERVICE_URL 'service:jmx:rmi://104.196.100.8:44445/jndi/rmi://104.196.100.8:44444/jmxrmi'
cf set-env platform-monitoring JMX_INTERVAL '60000'

cf start platform-monitoring
