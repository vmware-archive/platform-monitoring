package io.pivotal.plaformMonitoring.integration;

import io.pivotal.api.model.Metric;
import io.pivotal.plaformMonitoring.utils.DataPoint;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;

import static io.pivotal.plaformMonitoring.utils.DataPoint.addDataPoint;
import static io.pivotal.plaformMonitoring.utils.DataPointBuilder.dataPointBuilder;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class MainPageTest extends UiTest {
    @Before
    public void setup() {
        goTo(getBaseUrl());
        // wait for react to load into page
        await().atMost(2000L).until($(".main-page")).present();
    }

    @Test
    public void visit() {
        assertThat(pageSource()).contains("Logging Performance");
    }

    @Test
    public void hasData() throws Exception {
        DataPoint recievedEnvelopes = dataPointBuilder()
            .name(Metric.RECEIVED_ENVELOPES)
            .timestamp(System.currentTimeMillis())
            .value(3.0d)
            .build();

        addDataPoint(recievedEnvelopes, getJmxConnectorServer().getMBeanServer());

        Awaitility.await().atMost(1, SECONDS).until(() -> {
            assertThat($(".box-body").get(1).text()).isEqualTo("3");
        });
    }
}