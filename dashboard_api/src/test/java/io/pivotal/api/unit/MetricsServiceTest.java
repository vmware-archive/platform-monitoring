package io.pivotal.api.unit;

import io.pivotal.api.model.Metric;
import io.pivotal.api.service.CalculatorService;
import io.pivotal.api.service.JmxService;
import io.pivotal.api.service.MetricsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class MetricsServiceTest {
    @Mock
    private JmxService jmxService;

    @Mock
    private CalculatorService calculatorService;

    @Mock
    private Map<String, Double> store;

    @InjectMocks
    private MetricsService metricsService;

    @Test
    public void itStoresMetrics() throws Exception {
        Map<String, String> storeValues = new ConcurrentHashMap<>();
        storeValues.put(Metric.TOTAL_DROPPED_MESSAGES, "4.0");
        storeValues.put(Metric.SHED_ENVELOPES, "6.0");
        storeValues.put(Metric.RECEIVED_ENVELOPES, "10.0");

        when(jmxService.getMetrics()).thenReturn(storeValues);
        when(calculatorService.calculateFirehoseLossRate(anyObject())).thenReturn(1.0d);
        metricsService.run();

        verify(store, times(1)).put(Metric.TOTAL_DROPPED_MESSAGES, 4.0d);
        verify(store, times(1)).put(Metric.SHED_ENVELOPES, 6.0d);
        verify(store, times(1)).put(Metric.RECEIVED_ENVELOPES, 10.0d);
        verify(store, times(1)).put(Metric.CALCULATED_METRIC_FIREHOSE_LOSS_RATE, 1.0d);
    }
}