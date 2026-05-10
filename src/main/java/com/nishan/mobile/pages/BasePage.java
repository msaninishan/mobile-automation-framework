package com.nishan.mobile.pages;

import com.nishan.mobile.config.ConfigManager;
import com.nishan.mobile.core.DriverManager;
import io.appium.java_client.AppiumDriver;

public class BasePage {

    protected AppiumDriver driver;

    public BasePage() {
        driver = DriverManager.getDriver();
    }


    protected boolean isAndroid() {
        return DriverManager.getPlatform()
                .equalsIgnoreCase("android");
    }

    protected boolean isIOS() {
        return !isAndroid();
    }

    public void switchToWebView() {
    }

    public void switchToNative() {
    }

    public void switchToApp() {
    }
}
