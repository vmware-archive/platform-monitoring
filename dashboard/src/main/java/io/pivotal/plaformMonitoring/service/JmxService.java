package io.pivotal.plaformMonitoring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

@Service
public class JmxService {
    private final String OPENTSDB_PREFIX = "opentsdb.nozzle.";
    private JMXConnector jmxConnector;
    private String jmxServiceUrl;
    private String jmxUsername;
    private String jmxPassword;

    @Autowired
    public JmxService(String jmxServiceUrl, String jmxUsername, String jmxPassword) {
        this.jmxServiceUrl = jmxServiceUrl;
        this.jmxUsername = jmxUsername;
        this.jmxPassword = jmxPassword;
    }

    private JMXConnector createConnection(String url, String username, String password) throws IOException {
        JMXServiceURL serviceURL = new JMXServiceURL(url);
        String[] creds = new String[]{username, password};
        Map<String, String[]> env = new HashMap<>();
        env.put(JMXConnector.CREDENTIALS, creds);
        return JMXConnectorFactory.connect(serviceURL, env);
    }

    public Map<String, String> getMetrics() throws Exception {
        try {
            if(jmxConnector == null || jmxConnector.getConnectionId() == null) {
                jmxConnector = createConnection(jmxServiceUrl, jmxUsername, jmxPassword);
            }
        } catch(IOException e) {
            throw e;
        }
        Map<String, String> metrics = new ConcurrentHashMap<>();
        MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
        Set<ObjectName> names = mbeanConn.queryNames(new ObjectName("*:deployment=*,job=*,index=*,ip=*,*"), null);

        names.parallelStream()
            .forEach(name -> {
                try {
                    asList(mbeanConn.getMBeanInfo(name).getAttributes()).parallelStream()
                        .forEach(attr -> {
                            try {
                                metrics.put(attr.getName().replaceAll(OPENTSDB_PREFIX, ""), mbeanConn.getAttribute(name, attr.getName()).toString());
                            } catch(MBeanException | AttributeNotFoundException | ReflectionException | InstanceNotFoundException | IOException e) {
                                e.printStackTrace();
                            }
                        });
                } catch(InstanceNotFoundException | IntrospectionException | IOException | ReflectionException e) {
                    e.printStackTrace();
                }
            });

//        for(ObjectName name : names) {
//            System.out.println("LOOPING NAME");
//            for(MBeanAttributeInfo attr : mbeanConn.getMBeanInfo(name).getAttributes()) {
//                System.out.println("LOOPING ATTRIBUTE");
//                metrics.put(attr.getName().replaceAll(OPENTSDB_PREFIX, ""), mbeanConn.getAttribute(name, attr.getName()).toString());
//            }
//        }

        return metrics;
    }

    public void closeConnection() {
        try {
            if(jmxConnector != null) {
                jmxConnector.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
