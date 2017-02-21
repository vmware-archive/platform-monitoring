package io.pivotal.plaformMonitoring.utils;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by pivotal on 2/17/17.
 */
public class JMXNamingService {
    private static final List RESERVED_TAGS = Arrays.asList("id", "deployment", "job", "index", "ip", "name", "role");
    private static final String JMX_NAME_TEMPLATE = "org.cloudfoundry:deployment=%s,job=%s,index=%s,ip=%s";

    public String getName(DataPoint dataPoint) {
        String tagString = getTagString(dataPoint.getTags());
        return dataPoint.getMetricName() + tagString;
    }

    public ObjectName getJmxName(DataPoint dataPoint) throws MalformedObjectNameException {
        final String s = String.format(JMX_NAME_TEMPLATE, dataPoint.getTag("deployment"), dataPoint.getTag("job"), dataPoint.getTag("index"), dataPoint.getTag("ip"));
        return new ObjectName(s);
    }

    private String getTagString(Map<String, String> tags) {
        StringBuffer tagString = new StringBuffer("");
        for (String tag : new TreeSet<>(tags.keySet())) {
            if (!RESERVED_TAGS.contains(tag)) {
                tagString = tagString.append(tag).append("=").append(tags.get(tag)).append(",");
            }
        }
        if (tagString.length() > 1) {
            tagString.insert(0, "[").setCharAt(tagString.length() - 1, ']');
        }
        return tagString.toString();
    }
}