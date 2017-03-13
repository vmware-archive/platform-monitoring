package io.pivotal.plaformMonitoring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Metric {
    public final static String TOTAL_DROPPED_MESSAGES = "DopplerServer.TruncatingBuffer.totalDroppedMessages";
    public final static String SHED_ENVELOPES = "DopplerServer.doppler.shedEnvelopes";
    public final static String RECEIVED_ENVELOPES = "DopplerServer.listeners.receivedEnvelopes";
    public final static String CALCULATED_METRIC_FIREHOSE_LOSS_RATE = "calculatedMetric.Firehose.LossRate";

    private String name;
    private Double value;

    @JsonCreator
    public Metric(
        @JsonProperty("name") String name,
        @JsonProperty("value") Double value
    ) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Metric{" +
            "name='" + name + '\'' +
            ", value=" + value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Metric metric = (Metric) o;

        if(name != null ? !name.equals(metric.name) : metric.name != null) return false;
        return value != null ? value.equals(metric.value) : metric.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
