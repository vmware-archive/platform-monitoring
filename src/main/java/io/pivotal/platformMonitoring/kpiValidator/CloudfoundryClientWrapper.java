package io.pivotal.platformMonitoring.kpiValidator;

import com.jamonapi.MonitorFactory;
import org.apache.log4j.Logger;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import reactor.core.publisher.Flux;

import java.util.*;

public class CloudfoundryClientWrapper {
    private static Logger log = Logger.getLogger(CloudfoundryClientWrapper.class);
    public static MetricCounter getValueMetricsAndCounterEvents(String api, String username, String password, long duration) throws Exception{
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
        if(e.getEventType().equals(EventType.VALUE_METRIC)){
            return e.getOrigin()+"."+e.getValueMetric().getName();
        }else{
            return e.getOrigin()+"."+e.getCounterEvent().getName();
        }
    }
}
