package io.pivotal.plaformMonitoring.service;

import io.pivotal.plaformMonitoring.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MetricsService {
    @Autowired
    private JmxService jmxService;

    @Autowired
    private CalculatorService calculatorService;

    @Autowired
    private Map<String, Double> store;

    @Scheduled(fixedDelayString = "${jmx.interval}") // Make me use jmxInterval again
    public void run() throws Exception {
        System.out.println("Grabbing metrics!");

        try {
            System.out.println("start");
            jmxService.getMetrics()
                .forEach((k, v) -> store.put(k, Double.parseDouble(v)));
            System.out.println("end");

            store.put(Metric.CALCULATED_METRIC_FIREHOSE_LOSS_RATE, calculatorService.calculateFirehoseLossRate(store));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
