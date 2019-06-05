package io.pivotal.platformMonitoring.kpiValidator;

import org.apache.log4j.Logger;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import reactor.core.publisher.Flux;

import java.util.Calendar;
import java.util.UUID;

public class CloudfoundryClientWrapper {
    private static Logger log = Logger.getLogger(CloudfoundryClientWrapper.class);

    public static MetricCounter getValueMetricsAndCounterEvents(String api, String username, String password, long duration) throws Exception {
        log.info("Started Capturing Metrics: " + Calendar.getInstance().getTime() + " " + duration);
        MetricCounter metricCounter = new MetricCounter();
        DefaultConnectionContext defaultConnectionContext = DefaultConnectionContext.builder()
            .apiHost(api)
            .skipSslValidation(true)
            .build();

        PasswordGrantTokenProvider passwordGrantTokenProvider = PasswordGrantTokenProvider.builder()
            .password(password)
            .username(username)
            .build();

        ReactorDopplerClient reactorDopplerClient = ReactorDopplerClient.builder()
            .connectionContext(defaultConnectionContext)
            .tokenProvider(passwordGrantTokenProvider)
            .build();

        Flux<Envelope> cfEvents = reactorDopplerClient.firehose(
            FirehoseRequest.builder()
                .subscriptionId(UUID.randomUUID().toString()).build());
        cfEvents
            .filter(e -> e.getEventType().equals(EventType.COUNTER_EVENT) || e.getEventType().equals(EventType.VALUE_METRIC))
            .subscribe(e -> {
                metricCounter.addMetric(getName(e), e.getIndex());
            });
        Thread.sleep(duration);
        log.info("Stopped Capturing Metrics: " + Calendar.getInstance().getTime());
        return metricCounter;
    }

    private static String getName(Envelope e) {
        String origin = getOrigin(e);
        if (e.getEventType().equals(EventType.VALUE_METRIC)) {
            String name = e.getValueMetric().getName();
            return origin + "." + name;
        } else {
            String name = e.getCounterEvent().getName();
            return origin + "." + name + getDirection(e, origin, name);
        }
    }

    private static String getOrigin(Envelope e) {
        if (isNullOrEmpty(e.getOrigin())) {
            return e.getTags().get("source_id");
        } else {
            return e.getOrigin();
        }
    }

    private static String getDirection(Envelope e, String origin, String name) {
        if (origin.equals("loggregator.doppler") && name.equals("dropped") && e.getTags().containsKey("direction")) {
            return "." + e.getTags().get("direction");
        } else {
            return "";
        }
    }

    private static boolean isNullOrEmpty(String s) {
        if (s != null && !s.isEmpty()) {
            return false;
        }
        return true;
    }
}
