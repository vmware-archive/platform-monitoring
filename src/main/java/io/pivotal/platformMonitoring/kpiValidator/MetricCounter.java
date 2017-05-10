package io.pivotal.platformMonitoring.kpiValidator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class MetricCounter {
    private Map<String, Map<String, LongAdder>> map = new ConcurrentHashMap<>();

    public void addMetric(String name, String envelope){
       map.computeIfAbsent(name, k -> new ConcurrentHashMap<>());
       Map<String, LongAdder> innerMap = map.get(name);
        innerMap.computeIfAbsent(envelope, k -> new LongAdder()).increment();
    }

    public Map<String, Map<String, LongAdder>> getMetricMap(){
        return map;
    }

}
