package io.pivotal.platformMonitoring.kpiValidator;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Validator {

    private static final String HOSTNAME = System.getProperty("HOSTNAME","localhost:44444");
    private static final String USERNAME = System.getProperty("USERNAME","admin");
    private static final String PASSWORD = System.getProperty("PASSWORD","password");
    private static final String CF_DEPLOYMENT_NAME = System.getProperty("CF_DEPLOYMENT_NAME", "cf");
    private static final String NOZZLE_PREFIX = System.getProperty("NOZZLE_PREFIX","opentsdb.nozzle.");
    private static final int RUN_TIME = new Integer(System.getProperty("RUN_TIME_MINUTES","5"));
    private static final int POLL_INTERVAL = new Integer(System.getProperty("POLL_INTERVAL_SECONDS","5"));

    private JMXConnector jmxConnector;

    public static void main(String[] args){
        System.out.println("Running Validator");
        Validator validator = new Validator();
        try {
            validator.run();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() throws Exception{
        createConnection();
        Set<String> kpis = readMetrics();
        Set<String> receivedMetrics = new HashSet<>();
        Set<String> fullMetrics = new HashSet<>();
        MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
        String queryName = String.format("*:deployment=%1s*,job=*,index=*,ip=*,*", CF_DEPLOYMENT_NAME);
        System.out.println("Started Capturing Metrics: "+Calendar.getInstance().getTime());
        for(int i = 0; i < RUN_TIME*60/POLL_INTERVAL; i++){
            Set<ObjectName> names = mbeanConn.queryNames(new ObjectName(queryName), null);
            for(ObjectName name : names) {
                for (MBeanAttributeInfo attr : mbeanConn.getMBeanInfo(name).getAttributes()) {
                    receivedMetrics.add(attr.getName().replaceAll(NOZZLE_PREFIX, ""));
                    fullMetrics.add(name+":"+attr.getName().replaceAll(NOZZLE_PREFIX, ""));
                }
            }
            Thread.sleep(POLL_INTERVAL*1000);
        }
        System.out.println("Stopped Capturing Metrics: "+Calendar.getInstance().getTime());
        System.out.println("Received "+receivedMetrics.size()+" metrics.");
        closeConnection();

        System.out.println("*********************************RECEIVED METRICS***************************");
        List<String> sortedMetrics = new ArrayList<String>();
        sortedMetrics.addAll(fullMetrics);
        Collections.sort(sortedMetrics);
        for(String metric : sortedMetrics){
            System.out.println(metric);
        }
        System.out.println("*********************************DONE RECEIVED METRICS***************************");

        boolean missingKpis = false;
        try{
            PrintWriter writer = new PrintWriter("missing_kpis", "UTF-8");
            for(String metric : kpis){
                if(!metric.isEmpty() && !receivedMetrics.contains(metric)) {
                    missingKpis = true;
                    System.out.println("MISSING KPI: "+metric);
                    writer.println("MISSING KPI: "+metric);
                }
            }
            writer.close();
        } catch (IOException e) {
           e.printStackTrace();
        }
        if(missingKpis){
            System.out.println("THERE ARE MISSING KPIS!");
            System.exit(1);
        }else{
            System.out.println("There are no missing KPI's.  yay!");
            System.exit(0);
        }

    }
    


    private void createConnection() throws IOException {
        System.out.println("HOSTNAME: " + HOSTNAME);
        System.out.println("CF_DEPLOYMENT_NAME: " + CF_DEPLOYMENT_NAME);
        System.out.println("NOZZLE_PREFIX: " + NOZZLE_PREFIX);
        System.out.println("RUN_TIME_MINUTES: " + RUN_TIME);
        System.out.println("POLL_INTERVAL_SECONDS: "+POLL_INTERVAL);

        JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi://" + HOSTNAME + ":44445/jndi/rmi://" + HOSTNAME + ":44444/jmxrmi");
        String[] creds = new String[]{USERNAME, PASSWORD};
        Map<String, String[]> env = new HashMap<>();
        env.put(JMXConnector.CREDENTIALS, creds);
        jmxConnector = JMXConnectorFactory.connect(serviceURL, env);
    }

    private void closeConnection() throws IOException {
        if(jmxConnector != null) {
            jmxConnector.close();
        }
    }

    private Set<String> readMetrics(){
        String fileName = "kpis.txt";
        Set<String> metrics = new HashSet<>();
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            stream.forEach(metrics::add);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return metrics;
    }
}
