package io.pivotal.plaformMonitoring.unit;

import io.pivotal.plaformMonitoring.model.Metric;
import io.pivotal.plaformMonitoring.service.CalculatorService;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorServiceTest {
    private CalculatorService calculatorService;

    @Before
    public void setup() {
        calculatorService = new CalculatorService();
    }

    @Test
    public void itCalculatesLossRate() {
        Map<String, Double> storeValues = new ConcurrentHashMap<>();
        storeValues.put(Metric.TOTAL_DROPPED_MESSAGES, 2.0d);
        storeValues.put(Metric.SHED_ENVELOPES, 6.0d);
        storeValues.put(Metric.RECEIVED_ENVELOPES, 10.0d);

        double result = calculatorService.calculateFirehoseLossRate(storeValues);
        assertThat(result).isEqualTo(.8d);
    }
}
