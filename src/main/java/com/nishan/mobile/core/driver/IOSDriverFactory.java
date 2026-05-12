package com.nishan.mobile.core.driver;

import com.nishan.mobile.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URL;

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
        // app path same pattern as Android
        // Step 2 — resolve app path from classpath
        // convert relative path from config → absolute path
        String appPath = getClass().getClassLoader().getResource(config.get("ios.app")).getPath();
        options.setApp(appPath);
        // Step 3 — build server URL
        // config.get("appium.url") → new URL(...)
        URL url = null;
        try {
            url = new URL(config.get("appium.url"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        // Step 4 — return new AndroidDriver(serverUrl, options)
        return new IOSDriver(url, options);
    }
}
