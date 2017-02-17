#!/usr/bin/env bash


export JMX_USERNAME=root
export JMX_PASSWORD=root
export JMX_SERVICE_URL="service:jmx:rmi://104.196.221.222:44445/jndi/rmi://104.196.221.222:44444/jmxrmi"
export JMX_INTERVAL=5000

gradle clean && gradle bootRun
