package io.pivotal.api.service;

import io.pivotal.api.model.Metric;
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

    @Scheduled(fixedDelayString = "${jmx.interval}")
    public void run() throws Exception {
        System.out.println("Grabbing metrics!");

        try {
            jmxService.getMetrics()
                .forEach((k, v) -> store.put(k, Double.parseDouble(v)));

            store.put(Metric.CALCULATED_METRIC_FIREHOSE_LOSS_RATE, calculatorService.calculateFirehoseLossRate(store));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
