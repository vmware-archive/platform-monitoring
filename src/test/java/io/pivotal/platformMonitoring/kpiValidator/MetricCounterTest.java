package io.pivotal.platformMonitoring.kpiValidator;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class MetricCounterTest {
    private MetricCounter metricCounter;

    @Before
    public void setUp() throws Exception {
        metricCounter = new MetricCounter();
    }

    @Test
    public void it_returns_an_empty_Map() throws Exception {
        Map<String, Map<String, LongAdder>> map = metricCounter.getMetricMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(0);
    }

    @Test
    public void it_stores_a_new_metric() throws Exception {
        metricCounter.addMetric("some-name", "some-envelope");

        Map<String, Map<String, LongAdder>> map = metricCounter.getMetricMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(1);
        assertThat(map.get("some-name").get("some-envelope").intValue()).isEqualTo(1);
    }

    @Test
    public void it_stores_multiple_metrics_with_sameNameAndEnvelope() throws Exception {
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope");

        Map<String, Map<String, LongAdder>> map = metricCounter.getMetricMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(1);
        assertThat(map.get("some-name").get("some-envelope").intValue()).isEqualTo(2);
    }

    @Test
    public void it_stores_multiple_metrics_with_sameNameAndDifferentEnvelope() throws Exception {
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope1");
        metricCounter.addMetric("some-name", "some-envelope1");

        Map<String, Map<String, LongAdder>> map = metricCounter.getMetricMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(1);
        assertThat(map.get("some-name").size()).isEqualTo(2);
        assertThat(map.get("some-name").get("some-envelope").intValue()).isEqualTo(3);
        assertThat(map.get("some-name").get("some-envelope1").intValue()).isEqualTo(2);
    }

    @Test
    public void it_stores_multiple_metrics_with_differentNameAndDifferentEnvelope() throws Exception {
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name1", "some-envelope1");
        metricCounter.addMetric("some-name1", "some-envelope1");

        Map<String, Map<String, LongAdder>> map = metricCounter.getMetricMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get("some-name").size()).isEqualTo(1);
        assertThat(map.get("some-name").get("some-envelope").intValue()).isEqualTo(3);
        assertThat(map.get("some-name1").size()).isEqualTo(1);
        assertThat(map.get("some-name1").get("some-envelope1").intValue()).isEqualTo(2);
    }

    @Test
    public void it_stores_multiple_metrics_with_multipleNamesAndMultipleEnvelopes() throws Exception {
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope");
        metricCounter.addMetric("some-name", "some-envelope2");
        metricCounter.addMetric("some-name", "some-envelope2");
        metricCounter.addMetric("some-name", "some-envelope2");
        metricCounter.addMetric("some-name", "some-envelope3");
        metricCounter.addMetric("some-name", "some-envelope3");
        metricCounter.addMetric("some-name", "some-envelope3");
        metricCounter.addMetric("some-name", "some-envelope3");
        metricCounter.addMetric("some-name1", "some-envelope1");
        metricCounter.addMetric("some-name1", "some-envelope1");

        Map<String, Map<String, LongAdder>> map = metricCounter.getMetricMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get("some-name").size()).isEqualTo(3);
        assertThat(map.get("some-name").get("some-envelope").intValue()).isEqualTo(2);
        assertThat(map.get("some-name").get("some-envelope2").intValue()).isEqualTo(3);
        assertThat(map.get("some-name").get("some-envelope3").intValue()).isEqualTo(4);
        assertThat(map.get("some-name1").size()).isEqualTo(1);
        assertThat(map.get("some-name1").get("some-envelope1").intValue()).isEqualTo(2);
    }

}
