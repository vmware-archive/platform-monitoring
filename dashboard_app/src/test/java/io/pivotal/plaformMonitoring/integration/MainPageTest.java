package io.pivotal.plaformMonitoring.integration;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MainPageTest extends UiTest {
    @Before
    public void setup() {
        goTo(getBaseUrl());
    }

    @Test
    public void visit() {
        assertThat(pageSource()).contains("Logging Performance");
    }

    @Test
    public void clickBoxesChangesChart() {
        assertThat($(".panel-graph .title").get(0).text()).isEqualTo("Loss rate");
        $(".box").get(2).click();
        assertThat($(".panel-graph .title").get(0).text()).isEqualTo("Throughput");
    }
}