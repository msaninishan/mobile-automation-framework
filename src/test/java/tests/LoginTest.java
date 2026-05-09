package tests;

import com.nishan.mobile.config.ConfigManager;
import com.nishan.mobile.core.BaseTest;
import com.nishan.mobile.pages.HomePage;
import com.nishan.mobile.pages.LoginPage;
import com.nishan.mobile.pages.MenuPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    @Test
    public void validLoginTest() {
        HomePage home = new MenuPage()
                .navigateToLoginScreen()
                .enterUserName(ConfigManager.getInstance().get("userName"))
                .enterPassword(ConfigManager.getInstance().get("password"))
                .loginSuccessFully();
        Assert.assertNotNull(home);
    }

    @Test
    public void invalidLoginTest() {
        LoginPage home = new MenuPage()
                .navigateToLoginScreen()
                .enterUserName("nishan")
                .enterPassword("nishan")
                .loginExpectingFailure();
        System.out.println(new LoginPage().errorMessageDisplayed());
        Assert.assertEquals("Invalid login credentials, please try again",new LoginPage().errorMessageDisplayed());
    }
}
