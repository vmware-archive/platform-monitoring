package io.pivotal.plaformMonitoring.data;

import io.pivotal.plaformMonitoring.model.Metric;
import io.pivotal.plaformMonitoring.service.JmxService;
import io.pivotal.plaformMonitoring.utils.DataPoint;
import io.pivotal.plaformMonitoring.utils.DynamicMapMBean;
import io.pivotal.plaformMonitoring.utils.JMXNamingService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.Attribute;
import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.*;
import javax.security.auth.Subject;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

import static io.pivotal.plaformMonitoring.utils.DataPointBuilder.dataPointBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
public class JmxServiceTest {
    private static final String jmxServiceUrl = "service:jmx:rmi://localhost:44445/jndi/rmi://localhost:44444/jmxrmi";
    private static JMXConnectorServer jmxConnectorServer;

    private static JmxService jmxService;

    @BeforeClass
    public static void initialSetup() throws IOException {
        try {
            LocateRegistry.createRegistry(44444);
        } catch(RemoteException e) {
            e.printStackTrace();
        }

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        HashMap<String, Object> env = new HashMap<>();
        String[] creds = {"admin", "password"};
        env.put(JMXConnector.CREDENTIALS, creds);
        env.put(JMXConnectorServer.AUTHENTICATOR, new SimpleJMXAuthenticator("admin", "password"));

        JMXServiceURL url = new JMXServiceURL(jmxServiceUrl);
        jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, env, server);
        jmxConnectorServer.start();

        jmxService = new JmxService(jmxServiceUrl, "admin", "password");
    }

    @AfterClass
    public static void finalTeardown() throws IOException {
        jmxService.closeConnection();
        jmxConnectorServer.stop();
    }

    @Before
    public void setup() throws Exception {
        DataPoint shedEnvelopes = dataPointBuilder()
            .name(Metric.SHED_ENVELOPES)
            .timestamp(System.currentTimeMillis())
            .value(1.0d)
            .build();
        addDataPoint(shedEnvelopes, jmxConnectorServer.getMBeanServer());
    }

    @Test
    public void itReturnsMetrics() throws Exception {
        Map<String, String> receivedMetrics = jmxService.getMetrics();
        assertThat(receivedMetrics.keySet()).contains(Metric.SHED_ENVELOPES);
    }

    @Test
    public void itReusesConnections() throws Exception {
        Map<String, String> receivedMetrics = jmxService.getMetrics();
        assertThat(receivedMetrics.keySet().contains(Metric.SHED_ENVELOPES));
        receivedMetrics = jmxService.getMetrics();
        assertThat(receivedMetrics.keySet()).contains(Metric.SHED_ENVELOPES);
        assertThat(jmxConnectorServer.getConnectionIds()).hasSize(1);
    }

    @Test
    public void itClosesConnections() throws Exception {
        Map<String, String> receivedMetrics = jmxService.getMetrics();
        assertThat(receivedMetrics.keySet().contains(Metric.SHED_ENVELOPES));
        assertThat(jmxConnectorServer.getConnectionIds()).hasSize(1);
        jmxService.closeConnection();
        assertThat(jmxConnectorServer.getConnectionIds()).hasSize(0);
    }

    @Test
    public void itThrowsanExceptionWhenItCannotConnect() {
        JmxService foo = new JmxService(jmxServiceUrl, "blah", "password");
        assertThatExceptionOfType(SecurityException.class)
            .isThrownBy(foo::getMetrics);
    }


    private void addDataPoint(DataPoint dataPoint, MBeanServer server) throws Exception {
        DynamicMBean mBean = new DynamicMapMBean(dataPoint, new JMXNamingService());
        ObjectName mBeanName = new JMXNamingService().getJmxName(dataPoint);
        server.registerMBean(mBean, mBeanName);
        mBean.setAttribute(new Attribute(new JMXNamingService().getName(dataPoint), dataPoint.getValue()));
    }

    static class SimpleJMXAuthenticator implements JMXAuthenticator {

        private String username;

        private String password;

        public SimpleJMXAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public Subject authenticate(Object credentials) {
            Subject subject = null;
            if(credentials instanceof String[]) {
                String[] stringCredentials = (String[]) credentials;
                if(stringCredentials.length == 2 &&
                    username.equals(stringCredentials[0]) &&
                    password.equals(stringCredentials[1])) {
                    subject = new Subject();
                }
            }
            if(subject == null) {
                throw new SecurityException("Incorrect Username or Password");
            }
            return subject;
        }

    }
}