package io.pivotal.platformMonitoring.kpiValidator;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {
    public static final String KPI_FILE_NAME = "kpis.txt";
    public static final String MISSING_KPIS = "THERE ARE MISSING KPIS.";
    public static final String MISMATCHED_EMISSION_TIMES = "EMISSION TIMES ARE WRONG FOR KPIS.";
    private static final double RUN_TIME_MINUTES = new Double(System.getProperty("RUN_TIME_MINUTES", "5"));
    private static final double DEVIATION = new Double(System.getProperty("ALLOWED_DEVIATION", "3"));
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
            MetricCounter metricCounter = CloudfoundryClientWrapper.getValueMetricsAndCounterEvents(CF_API, CF_USERNAME, CF_PASSWORD, new Double(RUN_TIME_MINUTES * 60 * 1000).longValue());
            validator.run(metricCounter);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    public void run(MetricCounter metricCounter) throws Exception {
        HashSet<String> allGatheredMetrics = new HashSet<>();
        metricCounter.getMetricMap().keySet().stream().forEach(allGatheredMetrics::add);
        Map<String, Integer> kpiMap = readMetrics();

        print("RECEIVED METRICS", metricCounter.getMetricMap().keySet().stream());

        print("KPIS LIST", kpiMap.keySet().stream());

        List<String> missingKpis = kpiMap
            .entrySet()
            .stream()
            .filter(m -> !m.getKey().isEmpty())
            .filter(m -> !allGatheredMetrics.contains(m.getKey()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        if (missingKpis.isEmpty()) {
            log.info(NO_MISSING_KPIS);
        } else {
            print(MISSING_KPIS, missingKpis.stream());
            filePrint("missing_kpis", "MISSING KPI: %s%s", missingKpis.stream());
        }

        HashSet<String> mismatchedEmissionTimes = checkHits(kpiMap, metricCounter);
        print("MISMATCHED FREQUENCIES", mismatchedEmissionTimes.stream());

        if (mismatchedEmissionTimes.isEmpty()) {
            log.info(EMISSION_TIMES_CORRECT);
            filePrint("mismatched_times", "%s", Arrays.asList(EMISSION_TIMES_CORRECT).stream());
        } else {
            log.info(MISMATCHED_EMISSION_TIMES);
            filePrint("mismatched_times", "WRONG FREQUENCY: %s%s", mismatchedEmissionTimes.stream());
        }

        if (!missingKpis.isEmpty()) {
            throw new RuntimeException(MISSING_KPIS);
        } else if (!mismatchedEmissionTimes.isEmpty()) {
            throw new RuntimeException(MISMATCHED_EMISSION_TIMES);
        }
    }

    private static Map<String, Integer> readMetrics() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(KPI_FILE_NAME));
        Map<String, Integer> resultMap =
            lines.filter(line -> !line.startsWith("#"))
                .map(line -> line.split(","))
                .collect(Collectors.toMap(line -> line[0], line -> Integer.parseInt(line[1])));
        lines.close();
        return resultMap;
    }

    private void filePrint(String fileName, String format, Stream<String> stream) throws IOException {
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        stream
            .map(m -> String.format(format, m, System.lineSeparator()))
            .forEach(m -> {
                writer.write(m);
            });
        writer.close();
    }

    private void print(String banner, Stream<String> stream) {
        log.info("************" + banner + "**************");
        stream.sorted().forEach(log::info);
        log.info("***********************************");
    }

    private static HashSet<String> checkHits(Map<String, Integer> kpiMap, MetricCounter metricCounter) {
        Map<String, Map<String, LongAdder>> receivedMetrics = metricCounter.getMetricMap();

        HashSet<String> wrongEmissionTimes = new HashSet<>();

        kpiMap.keySet().stream()
            .forEach(name -> {
                if (receivedMetrics.containsKey(name)) {
                    double expectedHits = RUN_TIME_MINUTES * 60 / kpiMap.get(name);
                    receivedMetrics.get(name).keySet().stream().forEach(envelope -> {
                        int actualHits = receivedMetrics.get(name).get(envelope).intValue();
                        if (expectedHits > 0) {
                            if (actualHits < expectedHits - DEVIATION || actualHits > expectedHits + DEVIATION) {
                                wrongEmissionTimes.add(name + ":" + envelope + " actual hits: " + actualHits + " expected hits: " + expectedHits);
                            }
                        }
                    });


                }
            });
        return wrongEmissionTimes;
    }
}
