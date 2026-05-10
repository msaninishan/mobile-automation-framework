package com.nishan.mobile.listeners;

import com.nishan.mobile.core.DriverManager;
import com.nishan.mobile.retry.RetryAnalyzer;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver != null) {
            byte[] screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(
                    "Screenshot of failure - Attempt " +
                            (result.getMethod().getCurrentInvocationCount()),
                    "image/png",
                    new ByteArrayInputStream(screenShot),
                    "png");
        }

    }

    @Override
    public void onTestStart(ITestResult result) {
        // set retry analyzer on every test automatically
        result.getMethod()
                .setRetryAnalyzerClass(RetryAnalyzer.class);

        Allure.step("Starting the test: " + result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result.getMethod().getRetryAnalyzerClass() != null) {
            // this skip is actually a retry attempt
            // mark it as failed in Allure instead
            result.setStatus(ITestResult.FAILURE);
            onTestFailure(result);
        }
    }
}
