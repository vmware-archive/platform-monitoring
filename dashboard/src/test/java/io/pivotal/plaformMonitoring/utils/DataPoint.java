package io.pivotal.plaformMonitoring.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pivotal on 2/17/17.
 */
public class DataPoint {
    private final String name;
    private final Double value;
    private final Long timestamp;
    private final Map<String, String> tags;

    public DataPoint(String deployment, String job, String index, String ip, String name, double value, long timestamp) {
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("deployment", deployment);
        tags.put("job", job);
        tags.put("index", index);
        tags.put("ip", ip);

        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public String getMetricName() {
        return name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getValue() {
        return value;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String getTag(String tagName) {
        return tags.get(tagName);
    }
}
