package io.pivotal.plaformMonitoring.service;

import io.pivotal.plaformMonitoring.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by pivotal on 2/16/17.
 */
@Service
public class MetricsService implements CommandLineRunner {

    @Autowired
    private JMXService jmxService;
    private Configuration configuration;
    private boolean run = true;

    public MetricsService(JMXService jmxService, Configuration configuration) {
        this.jmxService = jmxService;
        this.configuration = configuration;
    }

    public void getMetrics() throws Exception {
        System.out.println("Retrieving Metrics...");
        Set<String> metrics = jmxService.getMetrics(configuration);
        for (String metric : metrics) {
            System.out.println(metric);
        }
        System.out.println("Done Retrieving Metrics.");
    }

    @Override
    public void run(String... args) throws Exception {
        // run a loop, every x seconds
        // get metrics from jmx bridge
        // print metrics to the console
        while (run) {
            try {
                getMetrics();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(configuration.getJmxInterval());
        }
    }

    public void stop() {
        run = false;
        System.out.println("stopping");
    }
}
