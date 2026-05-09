package com.nishan.mobile.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class HomePage extends BasePage {


    private By logoutbutton(){
        return AppiumBy.accessibilityId("Logout");
    }
}
