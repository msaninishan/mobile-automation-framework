package com.nishan.mobile.pages;

import com.nishan.mobile.utils.WaitUtility;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {


    private By userName() {
        return AppiumBy.accessibilityId("username");
    }

    private By password() {
        return AppiumBy.accessibilityId("password");
    }

    private By loginButton() {
        return AppiumBy.accessibilityId("loginBtn");
    }

    private By errorMessage() {
        return isAndroid()
                ? AppiumBy.id("android:id/message")
                : AppiumBy.accessibilityId("Invalid login credentials, please try again");
    }

    public LoginPage enterUserName(String userName) {
        WaitUtility.waitForElementVisible(userName());
        driver.findElement(userName()).clear();
        driver.findElement(userName()).sendKeys(userName);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WaitUtility.waitForElementVisible(password());
        driver.findElement(password()).clear();
        driver.findElement(password()).sendKeys(password);
        return this;
    }

    public HomePage loginSuccessFully() {
        WaitUtility.waitForElementClickable(loginButton());
        driver.findElement(loginButton()).click();
        return new HomePage();
    }

    public LoginPage loginExpectingFailure() {
        WaitUtility.waitForElementClickable(loginButton());
        driver.findElement(loginButton()).click();
        return this;
    }

    public String errorMessageDisplayed() {
        WaitUtility.waitForElementVisible(errorMessage());
        return driver.findElement(errorMessage()).getText();

    }
}
