package io.pivotal.plaformMonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlatformMonitoringApplication {

    public static void main(String[] args) {
        System.setProperty("jmx.interval", System.getenv("JMX_INTERVAL"));
        System.setProperty("jmx.serviceURL", System.getenv("JMX_SERVICE_URL"));
        System.setProperty("jmx.username", System.getenv("JMX_USERNAME"));
        System.setProperty("jmx.password", System.getenv("JMX_PASSWORD"));
        System.out.println("Running with System property: jmx.interval"+System.getenv("JMX_INTERVAL"));
        System.out.println("Running with System property: jmx.serviceURL"+System.getenv("JMX_SERVICE_URL"));
        System.out.println("Running with System property: jmx.username"+System.getenv("JMX_USERNAME"));
        System.out.println("Running with System property: jmx.password ********");
        SpringApplication.run(PlatformMonitoringApplication.class, args);
    }
}
