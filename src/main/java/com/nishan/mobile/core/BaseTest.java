package com.nishan.mobile.core;

import com.nishan.mobile.config.ConfigManager;
import org.testng.annotations.*;

public class BaseTest {

    @BeforeSuite
    protected void warmUp() {
        try {
            String platform = System.getProperty("platform",
                    ConfigManager.getInstance().get("platform"));
            DriverManager.initDriver(platform);
            System.out.println("Warm up complete");
            DriverManager.quitDriver();
        } catch (Exception e) {
            System.out.println("Warm up absorbed: " + e.getMessage());
        }
    }

    @BeforeMethod
    @Parameters({"platform"})
    protected void setUp(@Optional String platform) {
        // Priority 1: testng.xml parameter
        // Priority 2: -D system property
        // Priority 3: config.properties
        if (platform == null) {
            platform = System.getProperty("platform",
                    ConfigManager.getInstance().get("platform"));
        }
        DriverManager.initDriver(platform);
    }

    @AfterMethod
    protected void afterMethod() {
        DriverManager.quitDriver();
    }
}
