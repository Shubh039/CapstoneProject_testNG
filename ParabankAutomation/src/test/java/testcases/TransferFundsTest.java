package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.LoginPage;
import pages.TransferFundsPage;
import utilities.ExcelUtils;
import utilities.ExtentReportsManager;
import utilities.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class TransferFundsTest extends BaseTest {

    private static final Logger log = LoggerUtil.getLogger(TransferFundsTest.class);

    private static final String LOGIN_SHEET     = "LoginData";
    private static final String TRANSFER_AMOUNT = "100";

    private void loginWithExcelCredentials() {
        String username = ExcelUtils.getCellData(LOGIN_SHEET, 1, 0);
        String password = ExcelUtils.getCellData(LOGIN_SHEET, 1, 1);

        log.info("Logging in — Username: {}", username);
        ExtentReportsManager.getTest()
            .info("📂 Reading credentials from LoginData — Username: " + username);

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        Assert.assertTrue(
            loginPage.isLoginSuccessful(),
            "Login failed in TransferFundsTest setup"
        );

        log.info("Login successful ✅");
        ExtentReportsManager.getTest()
            .info("✅ Login successful — proceeding with transfer test");
    }

    // ─────────────────────────────────────────────
    //  TC01 — Valid Transfer with Balance Verification
    // ─────────────────────────────────────────────

    @Test(priority = 1,
          groups = {"smoke", "regression"},
          description = "TC01 - Valid transfer and verify balance changes")
    public void testValidTransferWithBalanceVerification() {

        loginWithExcelCredentials();

        // ── Step 1: Get account numbers ───────────────────────────────
        log.info("Navigating to Accounts Overview to get account numbers");
        ExtentReportsManager.getTest()
            .info("📊 Navigating to Accounts Overview");

        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();

        String fromAccount = overviewPage.getFirstAccountNumber();
        String toAccount   = overviewPage.getSecondAccountNumber();

        // ── Step 2: Record balances BEFORE ───────────────────────────
        double fromBalanceBefore = overviewPage.getAccountBalance(fromAccount);
        double toBalanceBefore   = overviewPage.getAccountBalance(toAccount);

        log.info("BEFORE TRANSFER:");
        log.info("FROM Account: {} | Balance: ${}", fromAccount, fromBalanceBefore);
        log.info("TO   Account: {} | Balance: ${}", toAccount,   toBalanceBefore);

        ExtentReportsManager.getTest()
            .info("💰 Balances BEFORE transfer:"
                + "<br>&nbsp;&nbsp;FROM Account : " + fromAccount + " | $" + fromBalanceBefore
                + "<br>&nbsp;&nbsp;TO   Account : " + toAccount   + " | $" + toBalanceBefore
                + "<br>&nbsp;&nbsp;Transfer Amount: $" + TRANSFER_AMOUNT);

        // ── Step 3: Perform transfer ──────────────────────────────────
        log.info("Performing transfer of ${} from {} to {}",
            TRANSFER_AMOUNT, fromAccount, toAccount);
        ExtentReportsManager.getTest()
            .info("🌐 Performing transfer: $" + TRANSFER_AMOUNT
                + " from " + fromAccount + " to " + toAccount);

        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        transferPage.transferFunds(TRANSFER_AMOUNT, fromAccount, toAccount);

        Assert.assertTrue(
            transferPage.isTransferSuccessful(),
            "Transfer failed — success message not shown"
        );

        log.info("Transfer completed successfully ✅");
        ExtentReportsManager.getTest()
            .info("✅ Transfer success message confirmed");

        // ── Step 4: Record balances AFTER ────────────────────────────
        log.info("Checking balances AFTER transfer");
        ExtentReportsManager.getTest()
            .info("📊 Navigating to Accounts Overview to verify updated balances");

        overviewPage.navigateToOverview();

        double fromBalanceAfter = overviewPage.getAccountBalance(fromAccount);
        double toBalanceAfter   = overviewPage.getAccountBalance(toAccount);

        log.info("AFTER TRANSFER:");
        log.info("FROM Account: {} | Balance: ${}", fromAccount, fromBalanceAfter);
        log.info("TO   Account: {} | Balance: ${}", toAccount,   toBalanceAfter);

        ExtentReportsManager.getTest()
            .info("💰 Balances AFTER transfer:"
                + "<br>&nbsp;&nbsp;FROM Account : " + fromAccount + " | $" + fromBalanceAfter
                + " (Expected: $" + (fromBalanceBefore - Double.parseDouble(TRANSFER_AMOUNT)) + ")"
                + "<br>&nbsp;&nbsp;TO   Account : " + toAccount   + " | $" + toBalanceAfter
                + " (Expected: $" + (toBalanceBefore + Double.parseDouble(TRANSFER_AMOUNT)) + ")");

        // ── Step 5: Verify balances ───────────────────────────────────
        double transferAmount = Double.parseDouble(TRANSFER_AMOUNT);
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
            fromBalanceAfter, fromBalanceBefore - transferAmount, 0.01,
            "FROM account balance not reduced correctly. "
            + "Expected: " + (fromBalanceBefore - transferAmount)
            + " Actual: " + fromBalanceAfter
        );

        softAssert.assertEquals(
            toBalanceAfter, toBalanceBefore + transferAmount, 0.01,
            "TO account balance not increased correctly. "
            + "Expected: " + (toBalanceBefore + transferAmount)
            + " Actual: " + toBalanceAfter
        );

        log.info("Balance verification passed ✅");
        ExtentReportsManager.getTest()
            .info("✅ Balance verification passed — amounts match expected values");

        // ── Step 6: Verify transaction activity ──────────────────────
        log.info("Verifying debit transaction in FROM account activity");
        ExtentReportsManager.getTest()
            .info("🔍 Checking transaction activity records");

        overviewPage.clickAccountNumber(fromAccount);
        boolean debitFound = overviewPage.isDebitTransactionPresent(TRANSFER_AMOUNT);

        softAssert.assertTrue(debitFound,
            "Debit transaction not found in FROM account activity");

        overviewPage.navigateToOverview();
        overviewPage.clickAccountNumber(toAccount);
        boolean creditFound = overviewPage.isCreditTransactionPresent(TRANSFER_AMOUNT);

        softAssert.assertTrue(creditFound,
            "Credit transaction not found in TO account activity");

        softAssert.assertAll();

        log.info("Debit transaction found in FROM account: {} ✅", debitFound);
        log.info("Credit transaction found in TO account: {} ✅", creditFound);

        ExtentReportsManager.getTest()
            .info("✅ Transaction activity verified:"
                + "<br>&nbsp;&nbsp;Debit  in FROM account : " + debitFound
                + "<br>&nbsp;&nbsp;Credit in TO   account : " + creditFound);

        // ── Step 7: Logout ────────────────────────────────────────────
        log.info("Logging out after transfer verification");
        ExtentReportsManager.getTest()
            .info("🚪 Logging out — TC01 complete");

        new LoginPage(getDriver()).logout();

        log.info("TC01_testValidTransferWithBalanceVerification completed ✅");
        ExtentReportsManager.getTest()
            .info("✅ TC01 Complete — All transfer verifications passed!");
    }
}