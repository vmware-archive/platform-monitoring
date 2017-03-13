#!/usr/bin/env bash


gradle clean && gradle build
cf push platform-monitoring -p build/libs/PlatformMonitoring-0.0.1-SNAPSHOT.jar --no-start

cf set-env platform-monitoring JMX_USERNAME 'root'
cf set-env platform-monitoring JMX_PASSWORD 'root'
cf set-env platform-monitoring JMX_SERVICE_URL 'service:jmx:rmi://35.185.60.162:44445/jndi/rmi://35.185.60.162:44444/jmxrmi'
cf set-env platform-monitoring JMX_INTERVAL '60000'
cf set-env platform-monitoring CALCULATION_INTERVAL '5000'

cf start platform-monitoring
