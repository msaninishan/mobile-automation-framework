package tests;

import com.nishan.mobile.config.ConfigManager;
import com.nishan.mobile.core.BaseTest;
import com.nishan.mobile.data.TestDataManager;
import com.nishan.mobile.data.User;
import com.nishan.mobile.pages.HomePage;
import com.nishan.mobile.pages.LoginPage;
import com.nishan.mobile.pages.MenuPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void validLoginTest() {
        User validUser = TestDataManager.getInstance()
                .getUser("validUser");
        HomePage home = new MenuPage()
                .handleCompatibilityScreen()
                .navigateToLoginScreen()
                .enterUserName(validUser.getUsername())
                .enterPassword(validUser.getPassword())
                .loginSuccessFully();
        Assert.assertNotNull(home);
    }

    @Test
    public void invalidLoginTest() {
        User invalidUser = TestDataManager.getInstance()
                .getUser("invalidUser");

        LoginPage home = new MenuPage()
                .handleCompatibilityScreen()
                .navigateToLoginScreen()
                .enterUserName(invalidUser.getUsername())
                .enterPassword(invalidUser.getPassword())
                .loginExpectingFailure();
        // System.out.println(new LoginPage().errorMessageDisplayed());
        Assert.assertEquals("Invalid login credentials, please try again", new LoginPage().errorMessageDisplayed());
    }
}
