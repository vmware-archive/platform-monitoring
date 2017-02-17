package io.pivotal.plaformMonitoring.service;

import io.pivotal.plaformMonitoring.configuration.Configuration;
import org.springframework.stereotype.Service;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by pivotal on 2/16/17.
 */
@Service
public class JMXService {

    private JMXConnector jmxConnector;

    private JMXConnector createConnection(String url, String username, String password) throws IOException {
        JMXServiceURL serviceURL = new JMXServiceURL(url);
        String[] creds = new String[]{username, password};
        Map<String, String[]> env = new HashMap<>();
        env.put(JMXConnector.CREDENTIALS, creds);
        return JMXConnectorFactory.connect(serviceURL, env);
    }

    public Set<String> getMetrics(Configuration configuration) throws Exception {
        try {
            if (jmxConnector == null || jmxConnector.getConnectionId() == null) {
                jmxConnector = createConnection(configuration.getJmxServiceURL(), configuration.getJmxUserName(), configuration.getJmxPassword());
            }
        } catch (IOException e) {
            //reconnect on IOException
            jmxConnector = createConnection(configuration.getJmxServiceURL(), configuration.getJmxUserName(), configuration.getJmxPassword());
        }
        MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
        Set<String> metrics = new HashSet<String>();
        Set<ObjectName> names = mbeanConn.queryNames(new ObjectName("*:deployment=*,job=*,index=*,ip=*,*"), null);
        for (ObjectName name : names) {
            for (MBeanAttributeInfo attr : mbeanConn.getMBeanInfo(name).getAttributes()) {
                metrics.add(attr.getName());
            }
        }
        return metrics;
    }

    public void closeConnection() {
        try {
            if (jmxConnector != null) {
                jmxConnector.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
