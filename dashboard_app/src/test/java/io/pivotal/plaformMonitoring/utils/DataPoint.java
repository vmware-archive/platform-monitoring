package io.pivotal.plaformMonitoring.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataPoint {
    private final String name;
    private final Double value;
    private final Long timestamp;
    private final Map<String, String> tags;

    public DataPoint(String name, double value, long timestamp) {
        Map<String, String> tags = new HashMap<>();
        tags.put("deployment", UUID.randomUUID().toString());
        tags.put("job", "some-job");
        tags.put("index", UUID.randomUUID().toString());
        tags.put("ip", "0.0.0.0");

        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
            "name='" + name + '\'' +
            ", value=" + value +
            ", timestamp=" + timestamp +
            ", tags=" + tags +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        DataPoint dataPoint = (DataPoint) o;

        if(name != null ? !name.equals(dataPoint.name) : dataPoint.name != null) return false;
        if(value != null ? !value.equals(dataPoint.value) : dataPoint.value != null) return false;
        if(timestamp != null ? !timestamp.equals(dataPoint.timestamp) : dataPoint.timestamp != null) return false;
        return tags != null ? tags.equals(dataPoint.tags) : dataPoint.tags == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }
}
