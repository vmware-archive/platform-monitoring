package io.pivotal.platformMonitoring.kpiValidator;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.pivotal.platformMonitoring.kpiValidator.Validator.KPI_FILE_NAME;

public class ValidatorTest {
    @Before
    public void setUp() throws Exception {
        System.setProperty("RUN_TIME_MINUTES", "0.1");
        System.setProperty("POLL_INTERVAL_SECONDS", "2");
        MetricCounter metricCounter = new MetricCounter();
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

    }

    @Test
    public void itExitsWithZeroIfNoMissingKPIs() throws Exception {
        new Validator().run(receivedMetrics());
    }

    @Test
    public void itExitsWithOneIfMissingKPIs() throws Exception {
        try {

            new Validator().run(new MetricCounter());
        } catch (RuntimeException e) {
            assert (e.getMessage()).equals(Validator.MISSING_KPIS);
        }
    }

    @Test
    public void itExitsWithOneIfBadFrequency() throws Exception {
        try {
            MetricCounter metricCounter = receivedMetrics();
            metricCounter.addMetric("route_emitter.RouteEmitterSyncDuration", "some-envelope-1");
            metricCounter.addMetric("route_emitter.RouteEmitterSyncDuration", "some-envelope-1");
            metricCounter.addMetric("route_emitter.RouteEmitterSyncDuration", "some-envelope-1");
            metricCounter.addMetric("route_emitter.RouteEmitterSyncDuration", "some-envelope-1");
            metricCounter.addMetric("route_emitter.RouteEmitterSyncDuration", "some-envelope-1");
            metricCounter.addMetric("route_emitter.RouteEmitterSyncDuration", "some-envelope-1");

            new Validator().run(metricCounter);
        } catch (RuntimeException e) {
            assert (e.getMessage()).equals(Validator.MISMATCHED_EMISSION_TIMES);
        }
    }

    private static MetricCounter receivedMetrics() throws IOException {
        MetricCounter metricCounter = new MetricCounter();
        Files.lines(Paths.get(KPI_FILE_NAME)).forEach(kpi -> metricCounter.addMetric(kpi.split(",")[0], "some-envelope-1"));
        return metricCounter;
    }

}
