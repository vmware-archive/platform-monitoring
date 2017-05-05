package io.pivotal.platformMonitoring.kpiValidator;

import com.jamonapi.MonKeyImp;
import com.jamonapi.MonitorFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {
    public static final String KPI_FILE_NAME = "kpis.txt";
    public static final String MISSING_KPIS = "THERE ARE MISSING KPIS.";
    public static final String MISMATCHED_EMISSION_TIMES = "EMISSION TIMES ARE WRONG FOR KPIS.";
    private static final double RUN_TIME_MINUTES = new Double(System.getProperty("RUN_TIME_MINUTES", "5"));
    private static final double DEVIATION = 2;
    private static final String NO_MISSING_KPIS = "There are no missing KPI's.  yay!";
    private static final String EMISSION_TIMES_CORRECT = "There are no wrong emission times. woot!";
    private static final String CF_API = System.getProperty("CF_API");
    private static final String CF_USERNAME = System.getProperty("CF_USERNAME");
    private static final String CF_PASSWORD = System.getProperty("CF_PASSWORD");
    private static Logger log = Logger.getLogger(Validator.class);

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

    private static Map<String, Integer> readMetrics() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(KPI_FILE_NAME));
        Map<String, Integer> resultMap =
            lines.map(line -> line.split(","))
                .collect(Collectors.toMap(line -> line[0], line -> Integer.parseInt(line[1])));
        lines.close();
        return resultMap;
    }

    public void run() throws Exception {
        HashSet<String> allGatheredMetrics = new HashSet<>();
        MonitorFactory.getMap().keySet().stream().forEach(name -> allGatheredMetrics.add(((MonKeyImp) name).getLabel()));

        Map<String, Integer> kpisList = readMetrics();
        Map<String, Integer> missingKpis = kpisList
            .entrySet()
            .stream()
            .filter(m -> !m.getKey().isEmpty())
            .filter(m -> !allGatheredMetrics.contains(m.getKey()))
            .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

        if(missingKpis.isEmpty()) {
            log.info(NO_MISSING_KPIS);
        } else {
            PrintWriter writer = new PrintWriter("missing_kpis", "UTF-8");

            missingKpis.keySet().stream()
                .map(m -> String.format("MISSING KPI: %s%s", m, System.lineSeparator()))
                .forEach(m -> {
                    log.info(m);
                    writer.write(m);
                });

            writer.close();

            log.info(MISSING_KPIS);
            throw new RuntimeException(MISSING_KPIS);
        }

        HashSet<String> mismatchedEmissionTimes = checkHits(kpisList, allGatheredMetrics);
        if(mismatchedEmissionTimes.isEmpty()) {
            log.info(EMISSION_TIMES_CORRECT);
        } else {
            PrintWriter writer = new PrintWriter("mismatchedTimes", "UTF-8");

            mismatchedEmissionTimes.stream()
                .map(m -> String.format("WRONG EMISSION TIME: %s%s", m, System.lineSeparator()))
                .forEach(m -> {
                    log.info(m);
                    writer.write(m);
                });

            writer.close();

            log.info(MISMATCHED_EMISSION_TIMES);
            throw new RuntimeException(MISMATCHED_EMISSION_TIMES);
        }
    }

    private static HashSet<String> checkHits(Map<String, Integer> kpisList, HashSet<String> allGatheredMetrics) {
        HashSet<String> wrongEmissionTimes = new HashSet<>();
        allGatheredMetrics.stream()
            .sorted()
            .forEach(name -> {
                if(kpisList.containsKey(name)) {
                    double expectedHits = RUN_TIME_MINUTES * 60 / kpisList.get(name);
                    double actualHits = MonitorFactory.getMonitor(name, "hits").getHits();
                    if(expectedHits < 0) { // this is handles by missing KPIS
                        return;
                    } else {
                        if(!(expectedHits-DEVIATION < actualHits && actualHits < expectedHits+DEVIATION)) {
                            System.out.println("bad time "+name+" hits  "+actualHits+" expected: "+expectedHits + " KPI VALUE: "+kpisList.get(name));
                            wrongEmissionTimes.add(name);
                        }
                    }
                }
            });
        return wrongEmissionTimes;
    }


}
