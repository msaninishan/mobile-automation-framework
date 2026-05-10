package com.nishan.mobile.pages;

import com.nishan.mobile.utils.WaitUtility;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class MenuPage extends BasePage {

    private By menuItem() {

        return AppiumBy.accessibilityId("Login Screen");
    }

    private By compatabilityScreen() {
        return AppiumBy.id("android:id/button2");

    }

    public MenuPage handleCompatibilityScreen() {
        if (isAndroid()) {
            WaitUtility.waitForElementClickable(compatabilityScreen());
            driver.findElement(compatabilityScreen()).click();
        }
        return this;
    }

    public LoginPage navigateToLoginScreen() {
        WaitUtility.waitForElementClickable(menuItem());
        driver.findElement(menuItem()).click();
        return new LoginPage();
    }

}
