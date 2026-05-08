import com.nishan.mobile.core.BaseTest;
import com.nishan.mobile.core.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest extends BaseTest {

    @Test
    public void validateAndroidSession() {
        AppiumDriver driver = DriverManager.getDriver();
        // just assert driver is not null
        // if this passes - session launched successfully
        Assert.assertNotNull(driver,
                "Driver should not be null - session failed to launch");
        System.out.println("Session ID: " + driver.getSessionId());
        System.out.println("Platform: " +
                driver.getCapabilities().getPlatformName());
    }
}
