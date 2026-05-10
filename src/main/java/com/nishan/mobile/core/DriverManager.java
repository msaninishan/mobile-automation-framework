package com.nishan.mobile.core;

import com.nishan.mobile.config.ConfigManager;
import com.nishan.mobile.core.driver.AndroidDriverFactory;
import com.nishan.mobile.core.driver.DriverFactory;
import com.nishan.mobile.core.driver.IOSDriverFactory;
import io.appium.java_client.AppiumDriver;

import java.time.Duration;

public class DriverManager {
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> platformThread = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver(String platform) {
        platformThread.set(platform);
        DriverFactory factory;
        switch (platform.toLowerCase()) {
            case "android" -> factory = new AndroidDriverFactory();
            case "ios" -> factory = new IOSDriverFactory();
            default ->
                    throw new RuntimeException("Unsuported platform:" + platform + "| Supported platforms are: android, ios");
        }
        AppiumDriver driver = factory.createDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(ConfigManager.getInstance().get("implicit.wait"))));
        driverThreadLocal.set(driver);

    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new RuntimeException("Driver not initialized. " + "Ensure initDriver() method is called in @BeforeMethod");
        }
        return driver;
    }

    public static String getPlatform() {
        return platformThread.get();
    }

    public static void quitDriver() {

        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
            platformThread.remove();
        }
    }
}
