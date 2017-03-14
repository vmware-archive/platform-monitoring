package io.pivotal.plaformMonitoring;

import io.pivotal.api.MetricsController;
import io.pivotal.api.model.Metric;
import io.pivotal.api.service.CalculatorService;
import io.pivotal.api.service.JmxService;
import io.pivotal.api.service.MetricsService;
import io.pivotal.plaformMonitoring.utils.DataPoint;
import io.pivotal.plaformMonitoring.utils.DynamicMapMBean;
import io.pivotal.plaformMonitoring.utils.JMXNamingService;
import io.pivotal.ui.HomeController;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.Attribute;
import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.List;

import static io.pivotal.plaformMonitoring.utils.DataPointBuilder.dataPointBuilder;
import static java.util.Collections.emptyMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IntegrationTest {
    private static JMXConnectorServer jmxConnectorServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeClass
    public static void initialSetup() throws IOException {
        try {
            LocateRegistry.createRegistry(44444);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost:44445/jndi/rmi://localhost:44444/jmxrmi");
        jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, emptyMap(), server);
        jmxConnectorServer.start();
    }

    @AfterClass
    public static void finalTeardown() throws IOException {
        jmxConnectorServer.stop();
    }

    @Test
    public void itReturnsMetrics() throws Exception {
        DataPoint shedEnvelopes = dataPointBuilder()
            .name(Metric.SHED_ENVELOPES)
            .timestamp(System.currentTimeMillis())
            .value(1.0d)
            .build();
        DataPoint droppedEnvelopes = dataPointBuilder()
            .name(Metric.TOTAL_DROPPED_MESSAGES)
            .timestamp(System.currentTimeMillis())
            .value(2.0d)
            .build();
        DataPoint recievedEnvelopes = dataPointBuilder()
            .name(Metric.RECEIVED_ENVELOPES)
            .timestamp(System.currentTimeMillis())
            .value(3.0d)
            .build();
        DataPoint ignoredDatapoint = dataPointBuilder()
            .name("something-that-should-be-ignored")
            .timestamp(System.currentTimeMillis())
            .value(17.0d)
            .build();
        addDataPoint(shedEnvelopes, jmxConnectorServer.getMBeanServer());
        addDataPoint(droppedEnvelopes, jmxConnectorServer.getMBeanServer());
        addDataPoint(recievedEnvelopes, jmxConnectorServer.getMBeanServer());
        addDataPoint(ignoredDatapoint, jmxConnectorServer.getMBeanServer());

        await().atMost(1, SECONDS).until(() -> {
            List<Metric> metrics = Arrays.asList(restTemplate.getForObject("/loggregator", Metric[].class));

            assertThat(metrics).hasSize(4);

            assertThat(metrics).contains(new Metric(Metric.SHED_ENVELOPES, 1.0d));
            assertThat(metrics).contains(new Metric(Metric.TOTAL_DROPPED_MESSAGES, 2.0d));
            assertThat(metrics).contains(new Metric(Metric.RECEIVED_ENVELOPES, 3.0d));
            assertThat(metrics).contains(new Metric(Metric.CALCULATED_METRIC_FIREHOSE_LOSS_RATE, 1.0d));
        });
    }

    private void addDataPoint(DataPoint dataPoint, MBeanServer server) throws Exception {
        DynamicMBean mBean = new DynamicMapMBean(dataPoint, new JMXNamingService());
        ObjectName mBeanName = new JMXNamingService().getJmxName(dataPoint);
        server.registerMBean(mBean, mBeanName);
        mBean.setAttribute(new Attribute(new JMXNamingService().getName(dataPoint), dataPoint.getValue()));
    }
}