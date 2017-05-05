package io.pivotal.platformMonitoring.kpiValidator;

import com.jamonapi.MonKeyImp;
import com.jamonapi.MonitorFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {
    private static Logger log = Logger.getLogger(Validator.class);
    public static final String KPI_FILE_NAME = "kpis.txt";
    public static final String MISSING_KPIS = "THERE ARE MISSING KPIS.";
    private static final double RUN_TIME_MINUTES = new Double(System.getProperty("RUN_TIME_MINUTES", "5"));
    private static final String NO_MISSING_KPIS = "There are no missing KPI's.  yay!";
    private static final String CF_API = System.getProperty("CF_API");
    private static final String CF_USERNAME = System.getProperty("CF_USERNAME");
    private static final String CF_PASSWORD = System.getProperty("CF_PASSWORD");

    public static void main(String[] args) {
        try {
            Validator validator = new Validator();
            log.info("Running validator");
            CloudfoundryClientWrapper.getValueMetricsAndCounterEvents(CF_API, CF_USERNAME, CF_PASSWORD, new Double(RUN_TIME_MINUTES * 60 * 1000).longValue());
            validator.run();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static Map<String, String> readMetrics() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(KPI_FILE_NAME));
        Map<String, String> resultMap = lines.map(line -> line.split(","))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
        lines.close();
        return resultMap;
    }

    public void run() throws Exception {
        HashSet<String> names = new HashSet<>();
        MonitorFactory.getMap().keySet().stream().forEach(name -> names.add(((MonKeyImp)name).getLabel()));

        names.stream()
            .sorted()
            .forEach(name -> {log.info(name+": "+MonitorFactory.getMonitor(name, "hits").getHits());});

        Map<String, String> missingKpis = readMetrics()
                .entrySet()
                .stream()
            .filter(m -> !m.getKey().isEmpty())
            .filter(m -> !names.contains(m.getKey()))
            .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

        System.out.println("**********************************");
        System.out.println(missingKpis.toString());
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        if(missingKpis.isEmpty()) {
            log.info(NO_MISSING_KPIS);
            return;
        } else {
//            PrintWriter writer = new PrintWriter("missing_kpis", "UTF-8");
//
//            missingKpis.stream()
//                .map(m -> String.format("MISSING KPI: %s%s", m, System.lineSeparator()))
//                .forEach(m -> {
//                    log.info(m);
//                    writer.write(m);
//                });
//
//            writer.close();

            log.info(MISSING_KPIS);
            throw new RuntimeException(MISSING_KPIS);

        }
    }
}
