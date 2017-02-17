package io.pivotal.plaformMonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlatformMonitoringApplication {

    public static void main(String[] args) {
        System.setProperty("jmx.interval", "30000");
        System.setProperty("jmx.serviceURL", "service:jmx:rmi://104.196.221.222:44445/jndi/rmi://104.196.221.222:44444/jmxrmi");
        System.setProperty("jmx.username", "root");
        System.setProperty("jmx.password", "root");
        SpringApplication.run(PlatformMonitoringApplication.class, args);
    }
}
