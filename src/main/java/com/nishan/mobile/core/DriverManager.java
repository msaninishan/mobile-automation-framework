package com.nishan.mobile.core;

import com.nishan.mobile.config.ConfigManager;
import com.nishan.mobile.core.driver.*;
import io.appium.java_client.AppiumDriver;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

public class DriverManager {
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> platformThread = new ThreadLocal<>();

    private static final Map<String, Supplier<DriverFactory>> LOCAL_REGISTRY = Map.of(
            "android", AndroidDriverFactory::new,
            "ios",     IOSDriverFactory::new
    );

    private static final Map<String, Supplier<DriverFactory>> CLOUD_REGISTRY = Map.of(
            "android", BrowserStackAndroidFactory::new,
            "ios",     BrowserStackIOSFactory::new
    );

    public static void initDriver(String platform) {
        platformThread.set(platform);

        String env = ConfigManager.getInstance()
                .get("execution.env");

        Map<String, Supplier<DriverFactory>> registry =
                env.equalsIgnoreCase("cloud")
                        ? CLOUD_REGISTRY
                        : LOCAL_REGISTRY;

        Supplier<DriverFactory> factorySupplier =
                registry.get(platform.toLowerCase());

        if (factorySupplier == null) {
            throw new RuntimeException(
                    "Unsupported platform: " + platform +
                            " | Supported: " + registry.keySet());
        }

        AppiumDriver driver = factorySupplier.get().createDriver();
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(
                        Long.parseLong(
                                ConfigManager.getInstance().get("implicit.wait"))));
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
