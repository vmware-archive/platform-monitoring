package io.pivotal.platformMonitoring.kpiValidator;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class Validator {
    public static final String KPI_FILE_NAME = "kpis.txt";
    private static final String CF_DEPLOYMENT_NAME = System.getProperty("CF_DEPLOYMENT_NAME", "cf");
    private static final String NOZZLE_PREFIX = System.getProperty("NOZZLE_PREFIX", "opentsdb.nozzle.");
    private static final double RUN_TIME_MINUTES = new Double(System.getProperty("RUN_TIME_MINUTES", "5"));
    private static final int POLL_INTERVAL_SECONDS = new Integer(System.getProperty("POLL_INTERVAL_SECONDS", "5"));
    private static final String QUERY_NAME = String.format("*:deployment=%1s*,job=*,index=*,ip=*,*", CF_DEPLOYMENT_NAME);
    public static final String MISSING_KPIS = "THERE ARE MISSING KPIS.";
    private static final String NO_MISSING_KPIS = "There are no missing KPI's.  yay!";
    private JmxConnectorManager jmxConnectionManager;

    public Validator(JmxConnectorManager jmxConnMan) {
        this.jmxConnectionManager = jmxConnMan;
    }

    public static void main(String[] args) {
        Validator validator = new Validator(new JmxConnectorManager());
        System.out.println("Running validator");

        try {
            validator.run();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    public void run() throws Exception {
        JMXConnector conn = jmxConnectionManager.getConnection();

        MBeanServerConnection mbeanConn = conn.getMBeanServerConnection();

        Set<String> receivedMetrics = new HashSet<>();
        Set<String> fullMetrics = new HashSet<>();

        System.out.println("Started Capturing Metrics: " + Calendar.getInstance().getTime() + " " + RUN_TIME_MINUTES);

        for(int i = 0; i < RUN_TIME_MINUTES * 60 / POLL_INTERVAL_SECONDS; i++) {
            Set<ObjectName> names = mbeanConn.queryNames(new ObjectName(QUERY_NAME), null);

            for(ObjectName name : names) {
                Arrays.stream(mbeanConn.getMBeanInfo(name).getAttributes())
                    .map(attr -> attr.getName().replaceAll(NOZZLE_PREFIX, ""))
                    .peek(receivedMetrics::add)
                    .map(attr -> String.format("%s:%s", name, attr))
                    .forEach(fullMetrics::add);
            }

            Thread.sleep(POLL_INTERVAL_SECONDS * 1000);
        }

        System.out.println("Stopped Capturing Metrics: " + Calendar.getInstance().getTime());
        System.out.println(String.format("Received %d metrics.", receivedMetrics.size()));
        jmxConnectionManager.closeConnection();

        fullMetrics.stream()
            .sorted()
            .forEach(System.out::println);

        List<String> missingKpis = readMetrics().stream()
            .filter(m -> !m.isEmpty())
            .filter(m -> !receivedMetrics.contains(m))
            .collect(toList());

        if(missingKpis.isEmpty()) {
            System.out.println(NO_MISSING_KPIS);
            return;
        } else {
            PrintWriter writer = new PrintWriter("missing_kpis", "UTF-8");

            missingKpis.stream()
                .map(m -> String.format("MISSING KPI: %s%s", m, System.lineSeparator()))
                .forEach(m -> {
                    System.out.println(m);
                    writer.write(m);
                });

            writer.close();

            System.out.println(MISSING_KPIS);
            throw new RuntimeException(MISSING_KPIS);

        }
    }

    private static List<String> readMetrics() throws IOException {
        return Files.lines(Paths.get(KPI_FILE_NAME))
            .collect(toList());
    }
}
