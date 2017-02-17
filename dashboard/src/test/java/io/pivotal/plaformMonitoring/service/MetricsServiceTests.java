package io.pivotal.plaformMonitoring.service;

import io.pivotal.plaformMonitoring.configuration.Configuration;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

/**
 * Created by pivotal on 2/16/17.
 */
@RunWith(SpringRunner.class)
public class MetricsServiceTests {

    @org.junit.Test
    public void itRetrievesMetricsOnaDefinedInterval() throws Exception {
        Configuration config = new Configuration();
        config.setJmxIntervalMilliseconds(500);

        JMXService jmxService = Mockito.spy(JMXService.class);
        Mockito.doReturn(new HashSet<>()).when(jmxService).getMetrics(Mockito.any(Configuration.class));
        MetricsService metricsService = new MetricsService(jmxService, config);
        MetricsRunner runner = new MetricsRunner(metricsService);
        Thread t = new Thread(runner);
        t.start();

        Thread.sleep(2000);
        metricsService.stop();
        Mockito.verify(jmxService, Mockito.atLeast(3)).getMetrics(config);
        Mockito.verify(jmxService, Mockito.atMost(5)).getMetrics(config);
    }

    class MetricsRunner implements Runnable {
        MetricsService metricsService;

        public MetricsRunner(MetricsService metricsService) {
            this.metricsService = metricsService;
        }

        @Override
        public void run() {
            try {
                metricsService.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}