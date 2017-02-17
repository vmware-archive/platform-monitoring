package io.pivotal.plaformMonitoring.configuration;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by pivotal on 2/17/17.
 */

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${jmx.interval}")
    private int jmxInterval;

    @Value("${jmx.serviceURL}")
    private String jmxServiceURL;

    @Value("${jmx.username}")
    private String jmxUserName;

    @Value("${jmx.password}")
    private String jmxPassword;

    public int getJmxInterval() {
        return jmxInterval;
    }

    public void setJmxIntervalMilliseconds(int jmxInterval) {
        this.jmxInterval = jmxInterval;
    }

    public String getJmxServiceURL() {
        return jmxServiceURL;
    }

    public void setJmxServiceURL(String jmxServiceURL) {
        this.jmxServiceURL = jmxServiceURL;
    }

    public String getJmxUserName() {
        return jmxUserName;
    }

    public void setJmxUserName(String jmxUserName) {
        this.jmxUserName = jmxUserName;
    }

    public String getJmxPassword() {
        return jmxPassword;
    }

    public void setJmxPassword(String jmxPassword) {
        this.jmxPassword = jmxPassword;
    }
}
