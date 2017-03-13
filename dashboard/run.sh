#!/usr/bin/env bash


export JMX_USERNAME=root
export JMX_PASSWORD=root
export JMX_SERVICE_URL="service:jmx:rmi://35.185.60.162:44445/jndi/rmi://35.185.60.162:44444/jmxrmi"
export JMX_INTERVAL=5000
export CALCULATION_INTERVAL=5000

gradle clean && gradle bootRun
