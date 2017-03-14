package io.pivotal.plaformMonitoring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "io.pivotal")
public class PlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);
    }

    @Bean
    public Map<String, Double> store() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Integer jmxInterval(@Value("${jmx.interval}") String jmxInterval) {
        System.out.println("jmxInterval: " + jmxInterval);
        return Integer.parseInt(jmxInterval);
    }

    @Bean
    public Integer calculationInterval(@Value("${calculation.interval}") String calculationInterval) {
        System.out.println("calculationInterval: " + calculationInterval);
        return Integer.parseInt(calculationInterval);
    }

    @Bean
    public String jmxServiceUrl(@Value("${jmx.serviceURL}") String jmxServiceUrl) {
        System.out.println("jmxServiceUrl: " + jmxServiceUrl);
        return jmxServiceUrl;
    }

    @Bean
    public String jmxUsername(@Value("${jmx.username}") String jmxUsername) {
        System.out.println("jmxUsername: " + jmxUsername);
        return jmxUsername;
    }

    @Bean
    public String jmxPassword(@Value("${jmx.password}") String jmxPassword) {
        System.out.println("jmxPassword: " + jmxPassword);
        return jmxPassword;
    }
}
