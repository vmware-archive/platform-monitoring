package io.pivotal.plaformMonitoring;

import io.pivotal.plaformMonitoring.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@RestController
public class MetricsController {
    private static final List<String> acceptedMetrics = asList(
        Metric.SHED_ENVELOPES,
        Metric.TOTAL_DROPPED_MESSAGES,
        Metric.RECEIVED_ENVELOPES,
        Metric.CALCULATED_METRIC_FIREHOSE_LOSS_RATE
    );
    @Autowired
    private Map<String, Double> store;

    @RequestMapping(method = RequestMethod.GET, value = "/loggregator", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Metric> firehoseLossRate() {
        return store.keySet().stream()
            .filter(acceptedMetrics::contains)
            .map(k -> new Metric(k, store.get(k)))
            .collect(toList());
    }
}
