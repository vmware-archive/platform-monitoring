package io.pivotal.platformMonitoring.kpiValidator;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JmxConnectorManager {
    private static final String HOSTNAME = System.getProperty("HOSTNAME", "localhost");
    private static final String USERNAME = System.getProperty("USERNAME", "admin");
    private static final String PASSWORD = System.getProperty("PASSWORD", "password");
    private static final String CF_DEPLOYMENT_NAME = System.getProperty("CF_DEPLOYMENT_NAME", "cf");
    private static final String NOZZLE_PREFIX = System.getProperty("NOZZLE_PREFIX", "opentsdb.nozzle.");
    private static final double RUN_TIME = new Double(System.getProperty("RUN_TIME_MINUTES", "5"));
    private static final int POLL_INTERVAL = new Integer(System.getProperty("POLL_INTERVAL_SECONDS", "5"));

    private static JMXConnector conn;

    public JMXConnector getConnection() throws IOException {
        if(conn != null) {
            return conn;
        }

        System.out.println("HOSTNAME: " + HOSTNAME);
        System.out.println("CF_DEPLOYMENT_NAME: " + CF_DEPLOYMENT_NAME);
        System.out.println("NOZZLE_PREFIX: " + NOZZLE_PREFIX);
        System.out.println("RUN_TIME_MINUTES: " + RUN_TIME);
        System.out.println("POLL_INTERVAL_SECONDS: " + POLL_INTERVAL);

        JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi://" + HOSTNAME + ":44445/jndi/rmi://" + HOSTNAME + ":44444/jmxrmi");
        String[] creds = new String[]{USERNAME, PASSWORD};
        Map<String, String[]> env = new HashMap<>();
        env.put(JMXConnector.CREDENTIALS, creds);

        conn = JMXConnectorFactory.connect(serviceURL, env);
        return conn;
    }

    public void closeConnection() throws IOException {
        if(conn != null) {
            conn.close();
        }
    }
}
