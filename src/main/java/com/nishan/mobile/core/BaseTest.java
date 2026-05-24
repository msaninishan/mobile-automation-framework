package com.nishan.mobile.core;

import com.nishan.mobile.config.ConfigManager;
import org.testng.annotations.*;

public class BaseTest {

   @BeforeMethod
@Parameters({"platform"})
protected void setUp(@Optional String platform) {

    // CI system property always wins — checked first
    String ciOverride = System.getProperty("platform");
    if (ciOverride != null) {
        platform = ciOverride;
    }

    // Fall back to testng.xml value, then config file default
    if (platform == null) {
        platform = ConfigManager.getInstance().get("platform");
    }

    DriverManager.initDriver(platform);
}

    @AfterMethod
    protected void afterMethod() {
        DriverManager.quitDriver();
    }
}
