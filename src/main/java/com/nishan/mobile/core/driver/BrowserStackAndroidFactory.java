package com.nishan.mobile.core.driver;

import com.nishan.mobile.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class BrowserStackAndroidFactory implements DriverFactory {

    @Override
    public AppiumDriver createDriver() {

        ConfigManager config = ConfigManager.getInstance();

        UiAutomator2Options options = new UiAutomator2Options();

        // Appium capabilities
        options.setPlatformName("Android");
        options.setApp(config.get("bs.android.app"));

        // BrowserStack options
        HashMap<String, Object> bsOptions = new HashMap<>();
//        bsOptions.put("username", config.get("bs.username"));
//        bsOptions.put("accessKey", config.get("bs.accesskey"));
        bsOptions.put("deviceName", "Google Pixel 8 Pro");
        bsOptions.put("osVersion", "14.0");
        bsOptions.put("projectName", "Mobile Automation Framework");
        bsOptions.put("buildName", "Android Build");
        bsOptions.put("sessionName", "Login Tests");
        bsOptions.put("debug", true);
        bsOptions.put("networkLogs", true);

        options.setCapability("bstack:options", bsOptions);
        System.out.println("username length = " + config.get("bs.username").length());
        System.out.println("accesskey length = " + config.get("bs.accesskey").length());
        System.out.println("HUB = [" + config.get("bs.server.url") + "]");
        System.out.println("=== BrowserStack Debug ===");
        System.out.println("App: " + config.get("bs.android.app"));
        System.out.println("OS Version: " + config.get("bs.android.platformVersion"));
        System.out.println("==========================");

        try {
           return new AndroidDriver(
                    new URL("https://"+config.get("bs.username")+":"+config.get("bs.accesskey")+"@hub.browserstack.com/wd/hub"),
                    options
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid BrowserStack URL", e);
        }
    }
}