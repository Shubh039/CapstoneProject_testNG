package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.BillPaymentPage;
import pages.LoginPage;
import utilities.ExcelUtils;
import utilities.ExtentReportsManager;
import utilities.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class BillPaymentTest extends BaseTest {
    private static final Logger log = LoggerUtil.getLogger(BillPaymentTest.class);

    private static final String LOGIN_SHEET   = "LoginData";
    private static final String PAYEE_NAME    = "Shubhank";
    private static final String PAYEE_ADDRESS = "123 Test Street";
    private static final String PAYEE_CITY    = "New York";
    private static final String PAYEE_STATE   = "NY";
    private static final String PAYEE_ZIP     = "10001";
    private static final String PAYEE_PHONE   = "9876543210";
    private static final String PAYEE_ACCOUNT = "13122";
    private static final String PAY_AMOUNT    = "100";

    private LoginPage loginWithExcelCredentials() {
        String username = ExcelUtils.getCellData(LOGIN_SHEET, 1, 0);
        String password = ExcelUtils.getCellData(LOGIN_SHEET, 1, 1);
        log.info("Logging in — Username: {}", username);
        ExtentReportsManager.getTest()
            .info("📂 Reading credentials from LoginData — Username: " + username);
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        Assert.assertTrue(
            loginPage.isLoginSuccessful(),
            "Login failed in BillPaymentTest setup"
        );

        log.info("Login successful ✅");
        ExtentReportsManager.getTest()
            .info("✅ Login successful — proceeding with Bill Payment test");
        return loginPage;
    }
    @Test(priority = 1,
          groups = {"smoke", "regression"},
          description = "TC01 - Valid bill payment and verify balance deduction")
    public void testValidBillPaymentWithBalanceVerification() {

        LoginPage loginPage = loginWithExcelCredentials();

        // ── Step 1: Record balance BEFORE payment ────────────────────
        log.info("Navigating to Accounts Overview to record balance before payment");
        ExtentReportsManager.getTest()
            .info("📊 Navigating to Accounts Overview");

        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();
        String fromAccount   = overviewPage.getFirstAccountNumber();
        double balanceBefore = overviewPage.getAccountBalance(fromAccount);

        log.info("FROM Account: {} | Balance Before: ${}", fromAccount, balanceBefore);
        ExtentReportsManager.getTest()
            .info("💰 Payment source details:"
                + "<br>&nbsp;&nbsp;Account Number : " + fromAccount
                + "<br>&nbsp;&nbsp;Balance Before : $" + balanceBefore
                + "<br>&nbsp;&nbsp;Payment Amount : $" + PAY_AMOUNT);

        // ── Step 2: Fill and submit bill payment ──────────────────────
        log.info("Navigating to Bill Pay page");
        ExtentReportsManager.getTest()
            .info("🌐 Navigating to Bill Pay page"
                + "<br>&nbsp;&nbsp;Payee Name    : " + PAYEE_NAME
                + "<br>&nbsp;&nbsp;Payee Account : " + PAYEE_ACCOUNT
                + "<br>&nbsp;&nbsp;Amount        : $" + PAY_AMOUNT
                + "<br>&nbsp;&nbsp;From Account  : " + fromAccount);

        BillPaymentPage billPayPage = new BillPaymentPage(getDriver());
        billPayPage.navigateToBillPay();
        billPayPage.fillAndSubmitBillPayment(
            PAYEE_NAME, PAYEE_ADDRESS, PAYEE_CITY, PAYEE_STATE,
            PAYEE_ZIP, PAYEE_PHONE, PAYEE_ACCOUNT, PAY_AMOUNT, fromAccount
        );

        log.info("Bill payment form submitted");

        Assert.assertTrue(
            billPayPage.isPaymentSuccessful(),
            "Bill payment failed — success message not shown"
        );

        log.info("Bill payment success message shown ✅");
        ExtentReportsManager.getTest()
            .info("✅ Bill payment success message confirmed");

        // ── Step 3: Verify success message content ────────────────────
        SoftAssert softAssert = new SoftAssert();

        String resultPayee  = billPayPage.getResultPayeeName();
        String resultAmount = billPayPage.getResultAmount();
        String resultAcct   = billPayPage.getResultAccountId();

        log.info("Success message — Payee: {} | Amount: {} | Account: {}",
            resultPayee, resultAmount, resultAcct);

        ExtentReportsManager.getTest()
            .info("🔍 Verifying success message content:"
                + "<br>&nbsp;&nbsp;Payee   : " + resultPayee   + " (Expected: " + PAYEE_NAME    + ")"
                + "<br>&nbsp;&nbsp;Amount  : " + resultAmount  + " (Expected: contains " + PAY_AMOUNT + ")"
                + "<br>&nbsp;&nbsp;Account : " + resultAcct    + " (Expected: " + fromAccount   + ")");

        softAssert.assertEquals(resultPayee, PAYEE_NAME,
            "DEFECT — Success message shows wrong payee name. "
            + "Expected: " + PAYEE_NAME + " Got: " + resultPayee);

        softAssert.assertTrue(resultAmount.contains(PAY_AMOUNT),
            "DEFECT — Success message shows wrong amount. "
            + "Expected to contain: " + PAY_AMOUNT + " Got: " + resultAmount);

        softAssert.assertEquals(resultAcct, fromAccount,
            "DEFECT — Success message shows wrong account. "
            + "Expected: " + fromAccount + " Got: " + resultAcct);

        log.info("Success message content verified ✅");
        ExtentReportsManager.getTest()
            .info("✅ Success message content verified correctly");

        // ── Step 4: Verify balance REDUCED ────────────────────────────
        log.info("Navigating to overview to verify balance reduction");
        ExtentReportsManager.getTest()
            .info("📊 Navigating to Accounts Overview to verify balance deduction");

        overviewPage.navigateToOverview();
        double balanceAfter   = overviewPage.getAccountBalance(fromAccount);
        double expectedBalance = balanceBefore - Double.parseDouble(PAY_AMOUNT);

        log.info("Balance AFTER payment: ${} | Expected: ${}", balanceAfter, expectedBalance);
        ExtentReportsManager.getTest()
            .info("💰 Balance verification:"
                + "<br>&nbsp;&nbsp;Before  : $" + balanceBefore
                + "<br>&nbsp;&nbsp;After   : $" + balanceAfter
                + "<br>&nbsp;&nbsp;Expected: $" + expectedBalance);

        softAssert.assertEquals(balanceAfter, expectedBalance, 0.01,
            "CRITICAL DEFECT — Balance not reduced after bill payment! "
            + "Expected: " + expectedBalance + " Actual: " + balanceAfter);

        log.info("Balance correctly reduced by ${} ✅", PAY_AMOUNT);
        ExtentReportsManager.getTest()
            .info("✅ Balance correctly reduced by $" + PAY_AMOUNT);

        // ── Step 5: Verify transaction in activity ────────────────────
        log.info("Checking account activity for bill payment transaction");
        ExtentReportsManager.getTest()
            .info("🔍 Checking account activity for 'Bill Payment to " + PAYEE_NAME + "'");

        overviewPage.clickAccountNumber(fromAccount);
        boolean transactionFound = overviewPage
            .isBillPaymentTransactionPresent(PAYEE_NAME);

        softAssert.assertTrue(transactionFound,
            "CRITICAL DEFECT — Bill Payment transaction not found in activity! "
            + "Expected: 'Bill Payment to " + PAYEE_NAME + "'");

        softAssert.assertAll();

        log.info("Bill payment transaction found in activity: {} ✅", transactionFound);
        ExtentReportsManager.getTest()
            .info("✅ Transaction 'Bill Payment to " + PAYEE_NAME
                + "' found in account activity");

        // ── Step 6: Logout ────────────────────────────────────────────
        log.info("Logging out after bill payment verification");
        ExtentReportsManager.getTest()
            .info("🚪 Logging out — TC01 complete");

        loginPage.logout();

        log.info("TC01_testValidBillPaymentWithBalanceVerification completed ✅");
        ExtentReportsManager.getTest()
            .info("✅ TC01 Complete — All bill payment verifications passed!");
    }
}