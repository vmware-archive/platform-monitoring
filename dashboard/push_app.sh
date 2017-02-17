#!/usr/bin/env bash


cf set-env platform-monitoring JMX_USERNAME 'root'
cf set-env platform-monitoring JMX_PASSWORD 'root'
cf set-env platform-monitoring JMX_SERVICE_URL 'service:jmx:rmi://104.196.221.222:44445/jndi/rmi://104.196.221.222:44444/jmxrmi'
cf set-env platform-monitoring JMX_INTERVAL '60000'

gradle clean && gradle build

cf push platform-monitoring -p build/libs/PlatformMonitoring-0.0.1-SNAPSHOT.jar