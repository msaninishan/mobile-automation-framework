package com.nishan.mobile.core.driver;

import com.nishan.mobile.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.remote.http.ClientConfig;

import java.net.URI;
import java.time.Duration;

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
        options.setNewCommandTimeout(Duration.ofSeconds(
                Long.parseLong(config.get("appium.newCommandTimeout"))));
        options.setUiautomator2ServerLaunchTimeout(
                Duration.ofMillis(Long.parseLong(
                        config.get("android.serverLaunchTimeout"))));
        options.setUiautomator2ServerInstallTimeout(
                Duration.ofMillis(Long.parseLong(
                        config.get("android.serverInstallTimeout"))));

        // Step 2 — resolve app path from classpath
        // convert relative path from config → absolute path
        String appPath = getClass().getClassLoader().getResource(config.get("android.app")).getPath();
        options.setApp(appPath);
        // Step 3 — build server URL
        // config.get("appium.url") → new URL(...)
        ClientConfig clientConfig = ClientConfig.defaultConfig()
                .baseUri(URI.create(config.get("appium.url")))
                .readTimeout(Duration.ofSeconds(300));
        // Step 4 — return new AndroidDriver(serverUrl, options)
        return new AndroidDriver(clientConfig, options);
    }
}
