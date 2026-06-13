package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.LoginPage;
import pages.OpenAccount;
import utilities.ExcelUtils;
import utilities.ExtentReportsManager;
import utilities.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class OpenAccountTest extends BaseTest {
    private static final Logger log = LoggerUtil.getLogger(OpenAccountTest.class);
    private static String LOGIN_SHEET   = "LoginData";
    private static String ACCOUNT_SHEET = "AccountData";
    
    private LoginPage loginWithExcelCredentials() {
        String username = ExcelUtils.getCellData(LOGIN_SHEET, 1, 0);
        String password = ExcelUtils.getCellData(LOGIN_SHEET, 1, 1);

        log.info("Logging in with credentials from Excel — Username: {}", username);
        ExtentReportsManager.getTest()
            .info("📂 Reading credentials from LoginData sheet — Username: " + username);

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        Assert.assertTrue(
            loginPage.isLoginSuccessful(),
            "Login failed before Open Account test"
        );

        log.info("Login successful ✅");
        ExtentReportsManager.getTest()
            .info("✅ Login successful — proceeding with Open Account test");

        return loginPage;
    }

    private void openAccountAndVerify(String accountType, LoginPage loginPage) {

        // ── Step 1: Record source account balance BEFORE ─────────────
        log.info("Navigating to Accounts Overview");
        ExtentReportsManager.getTest()
            .info("📊 Navigating to Accounts Overview to record source balance");

        AccountsOverviewPage accOverview = new AccountsOverviewPage(getDriver());
        accOverview.navigateToOverview();

        String sourceAccount = accOverview.getFirstAccountNumber();
        double sourceBalance  = accOverview.getAccountBalance(sourceAccount);

        log.info("Source Account: {} | Balance: ${}", sourceAccount, sourceBalance);
        ExtentReportsManager.getTest()
            .info("🏦 Source account details:"
                + "<br>&nbsp;&nbsp;Account Number : " + sourceAccount
                + "<br>&nbsp;&nbsp;Current Balance: $" + sourceBalance);

        // ── Step 2: Open new account ──────────────────────────────────
        log.info("Opening new {} account...", accountType);
        ExtentReportsManager.getTest()
            .info("🏦 Opening new " + accountType + " account...");

        OpenAccount openAcc = new OpenAccount(getDriver());
        openAcc.openNewAccount(accountType);

        Assert.assertTrue(
            openAcc.isAccountOpenedSuccessfully(),
            accountType + " account was not opened successfully"
        );

        String newAccountNumber = openAcc.getNewAccountNumber();

        log.info("New {} account created: {}", accountType, newAccountNumber);
        ExtentReportsManager.getTest()
            .info("✅ New " + accountType + " account opened successfully!"
                + "<br>&nbsp;&nbsp;New Account Number: " + newAccountNumber);

        // ── Step 3: Verify new account in overview ────────────────────
        log.info("Verifying new account {} appears in Accounts Overview", newAccountNumber);
        ExtentReportsManager.getTest()
            .info("🔍 Verifying new account visible in Accounts Overview");

        accOverview.navigateToOverview();

        Assert.assertTrue(
            accOverview.findAccountNumber(newAccountNumber),
            "DEFECT — Newly opened account " + newAccountNumber
            + " not visible in Accounts Overview!"
        );

        log.info("New account {} found in overview ✅", newAccountNumber);
        ExtentReportsManager.getTest()
            .info("✅ New account " + newAccountNumber + " visible in Accounts Overview");

        // ── Step 4: Verify new account balance = $100 ────────────────
        double newAccountBalance = accOverview.getAccountBalance(newAccountNumber);

        log.info("New account balance: ${}", newAccountBalance);
        ExtentReportsManager.getTest()
            .info("💰 New account balance: $" + newAccountBalance
                + " (Expected: $100.00)");

        Assert.assertEquals(
            newAccountBalance, 100.0, 0.01,
            "DEFECT — New account balance should be $100.00 but was $" + newAccountBalance
        );

        log.info("New account balance verified: $100.00 ✅");
        ExtentReportsManager.getTest()
            .info("✅ New account balance verified as $100.00");

        // ── Step 5: Verify source account reduced by $100 ────────────
        double balanceAfter = accOverview.getAccountBalance(sourceAccount);

        log.info("Source account balance after opening: ${}", balanceAfter);
        log.info("Expected: ${} | Actual: ${}",
            (sourceBalance - 100.0), balanceAfter);

        ExtentReportsManager.getTest()
            .info("💰 Source account balance verification:"
                + "<br>&nbsp;&nbsp;Before : $" + sourceBalance
                + "<br>&nbsp;&nbsp;After  : $" + balanceAfter
                + "<br>&nbsp;&nbsp;Expected: $" + (sourceBalance - 100.0));

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(
            balanceAfter,
            sourceBalance - 100.0,
            0.01,
            "DEFECT — Source account balance not reduced by $100. "
            + "Expected: $" + (sourceBalance - 100.0)
            + " Actual: $" + balanceAfter
        );
        softAssert.assertAll();

        log.info("Source account balance correctly reduced by $100 ✅");
        ExtentReportsManager.getTest()
            .info("✅ Source account balance correctly reduced by $100.00");

        // ── Step 6: Save to Excel ─────────────────────────────────────
        log.info("Saving account details to Excel — Account: {}, Type: {}",
            newAccountNumber, accountType);
        ExtentReportsManager.getTest()
            .info("📝 Saving new account details to AccountData sheet:"
                + "<br>&nbsp;&nbsp;Account Number: " + newAccountNumber
                + "<br>&nbsp;&nbsp;Account Type  : " + accountType);

        ExcelUtils.setCellData(ACCOUNT_SHEET, 1, 0, newAccountNumber);
        ExcelUtils.setCellData(ACCOUNT_SHEET, 1, 1, accountType);

        log.info("Account details saved to Excel ✅");
        ExtentReportsManager.getTest()
            .info("✅ Account details saved to AccountData sheet");

        // ── Step 7: Logout ────────────────────────────────────────────
        log.info("Logging out after {} account test", accountType);
        ExtentReportsManager.getTest()
            .info("🚪 Logging out — test complete");
        loginPage.logout();

        log.info("Open {} Account test completed successfully ✅", accountType);
        ExtentReportsManager.getTest()
            .info("✅ TC Complete — All Open " + accountType
                + " Account verifications passed!");
    }

    // Open Checking Account
    @Test(priority = 1,
          groups = {"smoke", "regression"},
          description = "TC01 - Open a new CHECKING account after login")
    public void TC01_testOpenCheckingAccount() {
        log.info("Starting TC01 — Open New CHECKING Account");
        ExtentReportsManager.getTest()
            .info("🏦 Starting TC01 — Open New CHECKING Account");

        LoginPage loginPage = loginWithExcelCredentials();
        openAccountAndVerify("CHECKING", loginPage);
    }
    // Open Savings Account
    @Test(priority = 2,
          groups = {"regression"},
          description = "TC02 - Open a new SAVINGS account after login")
    public void TC02_testOpenSavingsAccount() {
        log.info("Starting TC02 — Open New SAVINGS Account");
        ExtentReportsManager.getTest()
            .info("🏦 Starting TC02 — Open New SAVINGS Account");

        LoginPage loginPage = loginWithExcelCredentials();
        openAccountAndVerify("SAVINGS", loginPage);
    }
}