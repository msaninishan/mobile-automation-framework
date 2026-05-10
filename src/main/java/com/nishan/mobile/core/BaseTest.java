package com.nishan.mobile.core;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class BaseTest {


    @BeforeMethod
    @Parameters({"platform"})
    protected void setUp(@Optional("android") String platform) {
        DriverManager.initDriver(platform);
    }

    @AfterMethod
    protected void afterMethod() {
        DriverManager.quitDriver();
    }
}
