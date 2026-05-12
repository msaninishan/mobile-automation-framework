package com.nishan.mobile.core.driver;

import com.nishan.mobile.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.remote.http.ClientConfig;

import java.net.URI;
import java.time.Duration;

public class IOSDriverFactory implements DriverFactory {

    @Override
    public AppiumDriver createDriver() {
        ConfigManager config = ConfigManager.getInstance();
        XCUITestOptions options = new XCUITestOptions();
        options.setDeviceName(config.get("ios.deviceName"));
        options.setPlatformVersion(config.get("ios.platformVersion"));
        options.setAutomationName(config.get("ios.automationName"));
        options.setBundleId(config.get("ios.bundleId"));
        options.setNoReset(Boolean.parseBoolean(config.get("ios.noReset")));
        options.setNewCommandTimeout(Duration.ofSeconds(
                Long.parseLong(config.get("appium.newCommandTimeout"))));
        // app path same pattern as Android
        // Step 2 — resolve app path from classpath
        // convert relative path from config → absolute path
        String appPath = getClass().getClassLoader().getResource(config.get("ios.app")).getPath();
        options.setApp(appPath);
        // Step 3 — build server URL
        // config.get("appium.url") → new URL(...)

        ClientConfig clientConfig = ClientConfig.defaultConfig()
                .baseUri(URI.create(config.get("appium.url")))
                .readTimeout(Duration.ofSeconds(300));
        // Step 4 — return new AndroidDriver(serverUrl, options)
        return new IOSDriver(clientConfig, options);
    }
}
