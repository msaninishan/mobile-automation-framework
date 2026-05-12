package com.nishan.mobile.core.driver;

import com.nishan.mobile.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidDriverFactory implements DriverFactory {

    @Override
    public AppiumDriver createDriver() {
        ConfigManager config = ConfigManager.getInstance();

        // Step 1 — build capabilities
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(config.get("android.deviceName"));
        options.setPlatformVersion(config.get("android.platformVersion"));
        options.setAutomationName(config.get("android.automationName"));
        options.setAppPackage(config.get("android.appPackage"));
        options.setAppActivity(config.get("android.appActivity"));
        options.setNoReset(Boolean.parseBoolean(config.get("android.noReset")));

        // Step 2 — resolve app path from classpath
        // convert relative path from config → absolute path
        String appPath = getClass().getClassLoader().getResource(config.get("android.app")).getPath();
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
        return new AndroidDriver(url, options);
    }
}
