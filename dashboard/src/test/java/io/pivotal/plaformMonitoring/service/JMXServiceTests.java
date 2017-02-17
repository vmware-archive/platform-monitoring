package io.pivotal.plaformMonitoring.service;

import io.pivotal.plaformMonitoring.configuration.Configuration;
import io.pivotal.plaformMonitoring.utils.DataPoint;
import io.pivotal.plaformMonitoring.utils.DynamicMapMBean;
import io.pivotal.plaformMonitoring.utils.JMXNamingService;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.Attribute;
import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by pivotal on 2/16/17.
 */
@RunWith(SpringRunner.class)
public class JMXServiceTests {

    private Configuration configuration;

    public JMXServiceTests() {
        try {
            LocateRegistry.createRegistry(44444);
        } catch (Exception e) {
            //Will fail when run multiple times but doesn't matter
        }
        configuration = new Configuration();
        configuration.setJmxServiceURL("service:jmx:rmi://localhost:44445/jndi/rmi://localhost:44444/jmxrmi");
        configuration.setJmxUserName("admin");
        configuration.setJmxPassword("password");
    }

    @org.junit.Test
    public void itReturnsMetrics() throws IOException {
        //rmiRegistryPort = PortFinder.findFreePort(PortFinder.MIN_PORT_NUMBER, PortFinder.MAX_PORT_NUMBER);
        JMXConnectorServer jmxConnectorServer = null;
        try {
            jmxConnectorServer = createMBeanServer();
            addDataPoint(new DataPoint("some-deployment", "some-job", "some-guid", "0.0.0.0", "metric1", 1.1, 111111L), jmxConnectorServer.getMBeanServer());

            JMXService jmxService = new JMXService();
            Set<String> receivedMetrics = jmxService.getMetrics(configuration);
            assert (receivedMetrics.contains("metric1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            jmxConnectorServer.stop();
        }
    }

    @org.junit.Test
    public void itReusesConnections() throws IOException {

        //rmiRegistryPort = PortFinder.findFreePort(PortFinder.MIN_PORT_NUMBER, PortFinder.MAX_PORT_NUMBER);
        JMXConnectorServer jmxConnectorServer = null;
        try {
            jmxConnectorServer = createMBeanServer();
            addDataPoint(new DataPoint("some-other-deployment", "some-job", "some-guid", "0.0.0.0", "metric2", 1.1, 111111L), jmxConnectorServer.getMBeanServer());

            JMXService jmxService = new JMXService();
            Set<String> receivedMetrics = jmxService.getMetrics(configuration);
            assert (receivedMetrics.contains("metric2"));
            receivedMetrics = jmxService.getMetrics(configuration);
            assert (receivedMetrics.contains("metric2"));
            assertEquals(1, jmxConnectorServer.getConnectionIds().length);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            jmxConnectorServer.stop();
        }
    }

    @org.junit.Test
    public void itClosesConnections() throws IOException {

        //rmiRegistryPort = PortFinder.findFreePort(PortFinder.MIN_PORT_NUMBER, PortFinder.MAX_PORT_NUMBER);
        JMXConnectorServer jmxConnectorServer = null;
        try {
            jmxConnectorServer = createMBeanServer();
            addDataPoint(new DataPoint("some-other-other-deployment", "some-job", "some-guid", "0.0.0.0", "metric3", 1.1, 111111L), jmxConnectorServer.getMBeanServer());

            JMXService jmxService = new JMXService();
            Set<String> receivedMetrics = jmxService.getMetrics(configuration);
            assert (receivedMetrics.contains("metric3"));
            assertEquals(1, jmxConnectorServer.getConnectionIds().length);
            jmxService.closeConnection();
            assertEquals(0, jmxConnectorServer.getConnectionIds().length);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            jmxConnectorServer.stop();
        }
    }


    private JMXConnectorServer createMBeanServer() throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        JMXServiceURL serviceUrl = new JMXServiceURL(configuration.getJmxServiceURL());
        HashMap<String, String[]> env = new HashMap<>();
        String[] creds = {configuration.getJmxUserName(), configuration.getJmxPassword()};
        env.put(JMXConnector.CREDENTIALS, creds);
        JMXConnectorServer jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serviceUrl, env, mBeanServer);
        jmxConnectorServer.start();
        return jmxConnectorServer;
    }

    private void addDataPoint(DataPoint dataPoint, MBeanServer server) throws Exception {
        DynamicMBean mBean = new DynamicMapMBean(dataPoint, new JMXNamingService());
        ObjectName mBeanName = new JMXNamingService().getJmxName(dataPoint);
        server.registerMBean(mBean, mBeanName);
        mBean.setAttribute(new Attribute(new JMXNamingService().getName(dataPoint), dataPoint.getValue()));
        Set<ObjectName> names = server.queryNames(new ObjectName("*:deployment=some-deployment,job=*,index=*,ip=*,*"), null);
    }

}