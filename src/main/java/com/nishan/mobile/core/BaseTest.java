package com.nishan.mobile.core;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {



    @BeforeMethod
    protected void beforeMethod() {
        DriverManager.initDriver();
    }

    @AfterMethod
    protected void afterMethod() {
        DriverManager.quitDriver();
    }
}
