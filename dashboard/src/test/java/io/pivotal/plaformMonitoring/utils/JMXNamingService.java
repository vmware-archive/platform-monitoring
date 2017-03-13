package io.pivotal.plaformMonitoring.utils;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class JMXNamingService {
    private static final List RESERVED_TAGS = Arrays.asList("id", "deployment", "job", "index", "ip", "name", "role");
    private static final String JMX_NAME_TEMPLATE = "org.cloudfoundry:deployment=%s,job=%s,index=%s,ip=%s";

    public String getName(DataPoint dataPoint) {
        String tagString = getTagString(dataPoint.getTags());
        return dataPoint.getName() + tagString;
    }

    public ObjectName getJmxName(DataPoint dataPoint) throws MalformedObjectNameException {
        Map<String, String> tags = dataPoint.getTags();
        final String s = String.format(JMX_NAME_TEMPLATE, tags.get("deployment"), tags.get("job"), tags.get("index"), tags.get("ip"));
        return new ObjectName(s);
    }

    private String getTagString(Map<String, String> tags) {
        StringBuffer tagString = new StringBuffer("");
        for(String tag : new TreeSet<>(tags.keySet())) {
            if(!RESERVED_TAGS.contains(tag)) {
                tagString = tagString.append(tag).append("=").append(tags.get(tag)).append(",");
            }
        }
        if(tagString.length() > 1) {
            tagString.insert(0, "[").setCharAt(tagString.length() - 1, ']');
        }
        return tagString.toString();
    }
}