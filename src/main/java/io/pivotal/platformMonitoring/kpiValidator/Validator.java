package io.pivotal.platformMonitoring.kpiValidator;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

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
            Set<String> names = CloudfoundryClientWrapper.getValueMetricsAndCounterEvents(CF_API, CF_USERNAME, CF_PASSWORD, new Double(RUN_TIME_MINUTES * 60 * 1000).longValue());
            validator.run(names);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static List<String> readMetrics() throws IOException {
        return Files.lines(Paths.get(KPI_FILE_NAME))
            .collect(toList());
    }

    public void run(Set<String> names) throws Exception {
        names.stream()
            .sorted()
            .forEach(System.out::println);

        List<String> missingKpis = readMetrics().stream()
            .filter(m -> !m.isEmpty())
            .filter(m -> !names.contains(m))
            .collect(toList());

        if(missingKpis.isEmpty()) {
            log.info(NO_MISSING_KPIS);
            return;
        } else {
            PrintWriter writer = new PrintWriter("missing_kpis", "UTF-8");

            missingKpis.stream()
                .map(m -> String.format("MISSING KPI: %s%s", m, System.lineSeparator()))
                .forEach(m -> {
                    log.info(m);
                    writer.write(m);
                });

            writer.close();

            log.info(MISSING_KPIS);
            throw new RuntimeException(MISSING_KPIS);

        }
    }
}
