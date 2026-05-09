package com.nishan.mobile.utils;

import com.nishan.mobile.config.ConfigManager;
import com.nishan.mobile.core.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtility {
    private static final long timeout = Long.parseLong(ConfigManager.getInstance().get("explicit.wait"));

    private static AppiumDriver getDriver() {

        return DriverManager.getDriver();
    }

    public static void waitForElementVisible(By locator) {
        long timeout = Long.parseLong(ConfigManager.getInstance().get("explicit.wait"));
        new WebDriverWait(getDriver(), Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void waitForElementClickable(By locator) {
        long timeout = Long.parseLong(ConfigManager.getInstance().get("explicit.wait"));
        new WebDriverWait(getDriver(), Duration.ofSeconds(timeout)).until(ExpectedConditions.elementToBeClickable(locator));
    }

}
