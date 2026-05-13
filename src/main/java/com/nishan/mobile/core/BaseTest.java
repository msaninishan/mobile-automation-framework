package com.nishan.mobile.core;

import com.nishan.mobile.config.ConfigManager;
import org.testng.annotations.*;

public class BaseTest {

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
