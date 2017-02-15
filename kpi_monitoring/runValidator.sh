mvn clean
mvn package


java -DHOSTNAME=104.196.221.222 \
     -DUSERNAME=root \
     -DPASSWORD=root \
     -DCF_DEPLOYMENT_NAME=cf \
     -DNOZZLE_PREFIX=opentsdb.nozzle. \
     -DRUN_TIME_MINUTES=1 \
     -DPOLL_INTERVAL_SECONDS=5 \
    -jar target/platform-monitoring-validator-1.1-jar-with-dependencies.jar