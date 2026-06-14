package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.LoginPage;
import pages.RegistrationPage;
import utilities.ExcelUtils;
import utilities.ExtentReportsManager;
import utilities.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistrationTest extends BaseTest {
    private static final Logger log = LoggerUtil.getLogger(RegistrationTest.class);
    private static String REG_SHEET   = "Registration";
    private static String LOGIN_SHEET = "LoginData";

    @Test(priority = 1,
    	  groups = {"smoke", "regression"},
          description = "TC01 - Register a new user with valid data from Excel")
    public void TC01_testValidReg() {
        // ── Step 1: Read test data from Excel ────────────────────────
        log.info("Reading registration data from Excel sheet: {}", REG_SHEET);
        ExtentReportsManager.getTest().info("📂 Reading registration test data from Excel sheet: " + REG_SHEET);

        String firstName = ExcelUtils.getCellData(REG_SHEET, 1, 0);
        String lastName  = ExcelUtils.getCellData(REG_SHEET, 1, 1);
        String address   = ExcelUtils.getCellData(REG_SHEET, 1, 2);
        String city      = ExcelUtils.getCellData(REG_SHEET, 1, 3);
        String state     = ExcelUtils.getCellData(REG_SHEET, 1, 4);
        String zipCode   = ExcelUtils.getCellData(REG_SHEET, 1, 5);
        String phone     = ExcelUtils.getCellData(REG_SHEET, 1, 6);
        String ssn       = ExcelUtils.getCellData(REG_SHEET, 1, 7);
        String username = ExcelUtils.getCellData(REG_SHEET, 1, 8) + System.currentTimeMillis();
        String password = ExcelUtils.getCellData(REG_SHEET, 1, 9);
        log.info("Test data loaded successfully");
        log.info("Registering user: {}", username);

        ExtentReportsManager.getTest().info("✅ Test data loaded from Excel successfully");
        ExtentReportsManager.getTest().info("👤 Username to register: " + username);

        // ── Step 2: Navigate to Registration page ────────────────────
        log.info("Clicking Register link on home page");
        ExtentReportsManager.getTest().info("🌐 Clicking Register link on Parabank home page");
        RegistrationPage registerPage = new RegistrationPage(getDriver());
        registerPage.clickRegisterLink();

        // ── Step 3: Fill and submit registration form ─────────────────
        log.info("Filling registration form for user: {}", username);
        ExtentReportsManager.getTest()
            .info("📝 Filling registration form with the following data:"
                + "<br>&nbsp;&nbsp;Name    : " + firstName + " " + lastName
                + "<br>&nbsp;&nbsp;Address : " + address + ", " + city + ", " + state
                + "<br>&nbsp;&nbsp;Zip     : " + zipCode
                + "<br>&nbsp;&nbsp;Phone   : " + phone
                + "<br>&nbsp;&nbsp;Username: " + username);
        registerPage.fillAndSubmitRegistrationForm(
            firstName, lastName, address, city, state,zipCode, phone, ssn, username, password
        );
        log.info("Registration form submitted");

        // ── Step 4: Assert registration success ───────────────────────
        log.info("Verifying registration success message...");
        ExtentReportsManager.getTest().info("🔍 Verifying registration success message on page");
        boolean isRegistered = registerPage.isRegistrationSuccessful();

        if (isRegistered) {
            log.info("Registration successful for user: {} ✅", username);
            ExtentReportsManager.getTest().info("✅ Registration successful — success message displayed");
        } else {
            log.error("Registration FAILED for user: {}", username);
            ExtentReportsManager.getTest().fail("❌ Registration failed — success message NOT displayed");
        }

        Assert.assertTrue(isRegistered,
            "Registration failed! Success message not displayed for user: " + username);

        // ── Step 5: Save credentials to Excel ────────────────────────
        log.info("Saving credentials to LoginData sheet → {} / {}", username, password);
        ExtentReportsManager.getTest()
            .info("📝 Saving credentials to LoginData Excel sheet"
                + "<br>&nbsp;&nbsp;Username: " + username
                + "<br>&nbsp;&nbsp;Password: " + password);

        ExcelUtils.setCellData(LOGIN_SHEET, 1, 0, username);
        ExcelUtils.setCellData(LOGIN_SHEET, 1, 1, password);
        log.info("Credentials saved to LoginData sheet successfully");
        ExtentReportsManager.getTest()
            .info("✅ Credentials saved to LoginData sheet for use by LoginTest");

        // ── Step 6: View Account Overview ────────────────────────────
        log.info("Navigating to Account Overview after registration");
        ExtentReportsManager.getTest().info("📊 Navigating to Accounts Overview");

        log.info("╔════════════════════════════════════════════╗");
        log.info("║           YOUR ACCOUNT OVERVIEW            ║");
        log.info("╚════════════════════════════════════════════╝");

        AccountsOverviewPage accOverview = new AccountsOverviewPage(getDriver());
        accOverview.navigateToOverview();
        String holderName = accOverview.getAccountHolderName();
        String accNo      = accOverview.getFirstAccountNumber();
        double balance    = accOverview.getAccountBalance(accNo);

        log.info("Account Holder : {}", holderName);
        log.info("Account Number : {}", accNo);
        log.info("Account Balance: ${}", balance);
        ExtentReportsManager.getTest().info("🏦 Account Overview verified:"
                + "<br>&nbsp;&nbsp;Account Holder : " + holderName
                + "<br>&nbsp;&nbsp;Account Number : " + accNo
                + "<br>&nbsp;&nbsp;Account Balance: $" + balance);

        // ── Step 7: Logout ────────────────────────────────────────────
        log.info("Logging out after registration");
        ExtentReportsManager.getTest().info("🚪 Logging out — clean state for LoginTest");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.logout();

        log.info("TC01_testValidReg completed successfully ✅");
        ExtentReportsManager.getTest().info("✅ TC01 Complete — Registration verified and credentials saved");
    }
}