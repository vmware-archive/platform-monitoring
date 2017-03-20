package io.pivotal.plaformMonitoring.integration.config;

import org.openqa.selenium.WebDriver;

public class BrowserConfig {
    private final BrowserType browserType;
    private final boolean useHub;
    private final String hubLocation;

    public BrowserConfig(BrowserType browserType, boolean useHub, String hubLocation) {
        this.browserType = browserType;
        this.useHub = useHub;
        this.hubLocation = hubLocation;
    }

    public boolean usesHub() {
        return useHub;
    }

    public String getHubLocation() {
        return hubLocation;
    }

    public WebDriver resolveDriver(BrowserConfig browserConfig) {
        return browserConfig.usesHub()
            ? browserType.getRemoteWebDriver(browserConfig.getHubLocation())
            : browserType.getWebDriver();
    }

}
