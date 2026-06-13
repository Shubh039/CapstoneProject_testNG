package testcases;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import base.BaseTest;
import pages.LoginPage;
import utilities.ExcelUtils;
import utilities.ExtentReportsManager;
import utilities.LoggerUtil;
public class LoginTest extends BaseTest {
    private static final Logger log = LoggerUtil.getLogger(LoginTest.class);
    private static String LOGIN_SHEET = "LoginData";

    @Test(priority = 1,
          groups = {"smoke", "regression"},
          description = "TC02 - Login with valid credentials from Excel")
    public void TC01_validLogin() {
        // ── Step 1: Read credentials from Excel ──────────────────────
        log.info("Reading credentials from Excel sheet: {}", LOGIN_SHEET);
        ExtentReportsManager.getTest().info("📂 Reading credentials from Excel sheet: " + LOGIN_SHEET);
        String username = ExcelUtils.getCellData(LOGIN_SHEET, 1, 0);
        String password = ExcelUtils.getCellData(LOGIN_SHEET, 1, 1);

        log.info("Credentials read successfully — Username: {}", username);
        ExtentReportsManager.getTest().info("✅ Credentials read — Username: " + username);

        // ── Step 2: Perform login ─────────────────────────────────────
        log.info("Navigating to Parabank home page and logging in...");
        ExtentReportsManager.getTest().info("🌐 Attempting login for user: " + username);
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        // ── Step 3: Assert login success ──────────────────────────────
        boolean loginSuccess = loginPage.isLoginSuccessful();
        if (loginSuccess) {
            log.info("Login successful — redirected to overview.htm ✅");
            ExtentReportsManager.getTest().info("✅ Login successful — URL contains overview.htm");
        } else {
            log.error("Login FAILED for user: {}", username);
            ExtentReportsManager.getTest().fail("❌ Login failed for user: " + username);
        }

        Assert.assertTrue(loginSuccess,"Valid Login Failed for user: " + username);

        // ── Step 4: Logout ────────────────────────────────────────────
        log.info("Logging out after successful login verification");
        ExtentReportsManager.getTest().info("🚪 Logging out — cleaning state for next test");
        loginPage.logout();

        log.info("TC01_validLogin completed successfully ✅");
        ExtentReportsManager.getTest().info("✅ TC01 Complete — Login and logout verified successfully");
    }
}