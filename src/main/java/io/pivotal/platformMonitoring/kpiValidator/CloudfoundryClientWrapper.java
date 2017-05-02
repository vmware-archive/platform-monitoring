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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CloudfoundryClientWrapper {
    private static Logger log = Logger.getLogger(CloudfoundryClientWrapper.class);
    public static Set<String> getValueMetricsAndCounterEvents(String api, String username, String password, long duration) throws Exception{
        log.info("Started Capturing Metrics: " + Calendar.getInstance().getTime() + " " + duration);
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
        Set<String> names = new HashSet<>();
        cfEvents
            .filter(e -> e.getEventType().equals(EventType.COUNTER_EVENT) || e.getEventType().equals(EventType.VALUE_METRIC))
            .subscribe(e -> {
                addName(names, e);
            });
        Thread.sleep(duration);
        log.info("Stopped Capturing Metrics: " + Calendar.getInstance().getTime());
        log.info(String.format("Received %d metrics.", names.size()));
        return names;
    }

    private static void addName(Set<String> names, Envelope e) {
        if(e.getEventType().equals(EventType.VALUE_METRIC)){
            names.add(e.getOrigin()+"."+e.getValueMetric().getName());
        }else{
            names.add(e.getOrigin()+"."+e.getCounterEvent().getName());
        }
    }
}
