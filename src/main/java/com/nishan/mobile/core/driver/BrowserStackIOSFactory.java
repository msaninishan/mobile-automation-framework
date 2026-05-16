package com.nishan.mobile.core.driver;

import com.nishan.mobile.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class BrowserStackIOSFactory implements DriverFactory {

    @Override
    public AppiumDriver createDriver() {
        ConfigManager config = ConfigManager.getInstance();

        XCUITestOptions options = new XCUITestOptions();

        HashMap<String, Object> bsOptions = new HashMap<>();
        bsOptions.put("userName", config.get("bs.username"));
        bsOptions.put("accessKey", config.get("bs.accesskey"));
        bsOptions.put("deviceName", config.get("bs.ios.deviceName"));
        bsOptions.put("osVersion", config.get("bs.ios.platformVersion"));
        bsOptions.put("app", config.get("bs.ios.app"));
        bsOptions.put("projectName", "Mobile Automation Framework");
        bsOptions.put("buildName", "iOS Build");
        bsOptions.put("sessionName", "Login Tests");
        bsOptions.put("debug", true);
        bsOptions.put("networkLogs", true);

        options.setCapability("bstack:options", bsOptions);

        try {
            return new IOSDriver(
                    new URL(config.get("bs.server.url")), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException(
                    "Invalid BrowserStack server URL", e);
        }
    }
}