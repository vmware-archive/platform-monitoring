package io.pivotal.platformMonitoring.kpiValidator;

import io.pivotal.platformMonitoring.kpiValidator.utils.DynamicMapMBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static io.pivotal.platformMonitoring.kpiValidator.Validator.KPI_FILE_NAME;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JmxConnectorManager.class)
public class ValidatorTest {
    private MBeanServer mBeanServer;
    private Validator validator;

    @Before
    public void setUp() throws Exception {
        System.setProperty("RUN_TIME_MINUTES", "0.1");
        System.setProperty("POLL_INTERVAL_SECONDS", "2");

        mBeanServer = MBeanServerFactory.newMBeanServer();

        JMXConnector mockConnection = mock(JMXConnector.class);
        when(mockConnection.getMBeanServerConnection()).thenReturn(mBeanServer);

        JmxConnectorManager connMan = mock(JmxConnectorManager.class);
        when(connMan.getConnection()).thenReturn(mockConnection);
        validator = new Validator(connMan);
    }

    @Test
    public void itExitsWithZeroIfNoMissingKPIs() throws Exception {
        addMetrics();
        validator.run();
    }

    @Test
    public void itExitsWithOneIfMissingKPIs() throws Exception{
        try {
            validator.run();
        } catch( RuntimeException e) {
            assert(e.getMessage()).equals(Validator.MISSING_KPIS);
        }
    }


    private static List<String> readMetrics() throws IOException {
        return Files.lines(Paths.get(KPI_FILE_NAME))
            .collect(toList());
    }

    private static void addMetric(String name, MBeanServer server) throws Exception {
        DynamicMBean mBean = new DynamicMapMBean(name);
        ObjectName mBeanName = new ObjectName("org.cloudfoundry:deployment=cf,job=b,index=c,ip="+name);
        server.registerMBean(mBean, mBeanName);
        mBean.setAttribute(new Attribute(name, 0.0));
    }

    public void addMetrics() throws Exception {
        List<String> kpis = readMetrics();
        for (String kpi : kpis) {
            registerBean(kpi);
        }
    }

    private boolean registerBean(String name) {
        try {
            addMetric(name, mBeanServer);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public MBeanServer getPlatformMbeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
