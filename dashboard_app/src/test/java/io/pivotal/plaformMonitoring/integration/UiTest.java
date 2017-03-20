package io.pivotal.plaformMonitoring.integration;

import io.pivotal.plaformMonitoring.integration.config.BrowserConfig;
import io.pivotal.plaformMonitoring.integration.config.BrowserType;
import org.fluentlenium.adapter.junit.FluentTest;
import org.fluentlenium.core.hook.wait.Wait;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import static java.util.Collections.emptyMap;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Wait
public abstract class UiTest extends FluentTest {
    private static JMXConnectorServer jmxConnectorServer;

    @Value("${selenium.browser.type}")
    private BrowserType browserType;

    @Value("${selenium.hub.enabled}")
    private Boolean useHub;

    @Value("${selenium.hub.location}")
    private String hubLocation;

    @Value("${local.server.port}")
    private int port;

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

    @Override
    public WebDriver newWebDriver() {
        BrowserConfig browserConfig = new BrowserConfig(browserType, useHub, hubLocation);
        return browserConfig.resolveDriver(browserConfig);
    }

    @Override
    public String getBaseUrl() {
        return String.format("localhost:%s", port);
    }
}
