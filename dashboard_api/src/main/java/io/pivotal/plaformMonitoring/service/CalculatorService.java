package io.pivotal.plaformMonitoring.service;

import io.pivotal.plaformMonitoring.model.Metric;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CalculatorService {
    public double calculateFirehoseLossRate(Map<String, Double> metrics) {
        Double dropped = metrics.getOrDefault(Metric.TOTAL_DROPPED_MESSAGES, 0.0d);
        Double shed = metrics.getOrDefault(Metric.SHED_ENVELOPES, 0.0d);
        Double received = metrics.getOrDefault(Metric.RECEIVED_ENVELOPES, 1.0d);

        return (dropped + shed) / received;
    }
}
