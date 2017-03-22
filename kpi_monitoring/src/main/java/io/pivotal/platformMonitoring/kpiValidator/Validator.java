package io.pivotal.platformMonitoring.kpiValidator;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static io.pivotal.platformMonitoring.kpiValidator.JmxConnectorManager.closeConnection;
import static io.pivotal.platformMonitoring.kpiValidator.JmxConnectorManager.getConnection;
import static java.util.stream.Collectors.toList;

public class Validator {
    private static final String CF_DEPLOYMENT_NAME = System.getProperty("CF_DEPLOYMENT_NAME", "cf");
    private static final String NOZZLE_PREFIX = System.getProperty("NOZZLE_PREFIX", "opentsdb.nozzle.");
    private static final int RUN_TIME_MINUTES = new Integer(System.getProperty("RUN_TIME_MINUTES", "5"));
    private static final int POLL_INTERVAL_SECONDS = new Integer(System.getProperty("POLL_INTERVAL_SECONDS", "5"));
    private static final String QUERY_NAME = String.format("*:deployment=%1s*,job=*,index=*,ip=*,*", CF_DEPLOYMENT_NAME);
    private static final String KPI_FILE_NAME = "kpis.txt";

    public static void main(String[] args) {
        System.out.println("Running validator");

        try {
            Validator.run();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void run() throws Exception {
        JMXConnector conn = getConnection();
        MBeanServerConnection mbeanConn = conn.getMBeanServerConnection();

        Set<String> receivedMetrics = new HashSet<>();
        Set<String> fullMetrics = new HashSet<>();

        System.out.println("Started Capturing Metrics: " + Calendar.getInstance().getTime());

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
        closeConnection();

        fullMetrics.stream()
            .sorted()
            .forEach(System.out::println);

        List<String> missingKpis = readMetrics().stream()
            .filter(m -> !m.isEmpty())
            .filter(m -> !receivedMetrics.contains(m))
            .collect(toList());

        if(missingKpis.isEmpty()) {
            System.out.println("There are no missing KPI's.  yay!");
            System.exit(0);
        } else {
            PrintWriter writer = new PrintWriter("missing_kpis", "UTF-8");

            missingKpis.stream()
                .map(m -> String.format("MISSING KPI: %s", m))
                .forEach(m -> {
                    System.out.println(m);
                    writer.write(m);
                });

            writer.close();

            System.out.println("THERE ARE MISSING KPIS");
            System.exit(1);
        }
    }

    private static List<String> readMetrics() throws IOException {
        return Files.lines(Paths.get(KPI_FILE_NAME))
            .collect(toList());
    }
}
