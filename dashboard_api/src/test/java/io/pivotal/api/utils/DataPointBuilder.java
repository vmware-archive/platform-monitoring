package io.pivotal.api.utils;

public class DataPointBuilder {
    private String name;
    private Double value;
    private Long timestamp;

    public static DataPointBuilder dataPointBuilder() {
        return new DataPointBuilder();
    }

    public DataPointBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DataPointBuilder value(Double value) {
        this.value = value;
        return this;
    }

    public DataPointBuilder timestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public DataPoint build() {
        return new DataPoint(name, value, timestamp);
    }
}
