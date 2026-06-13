package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.LoginPage;
import pages.RequestLoanPage;
import utilities.ExcelUtils;
import utilities.ExtentReportsManager;
import utilities.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class RequestLoanTest extends BaseTest {

    private static final Logger log = LoggerUtil.getLogger(RequestLoanTest.class);

    private static final String LOGIN_SHEET = "LoginData";

    // ─────────────────────────────────────────────
    //  HELPER — Login
    // ─────────────────────────────────────────────
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
            "Login failed in RequestLoanTest"
        );

        log.info("Login successful ✅");
        ExtentReportsManager.getTest()
            .info("✅ Login successful — proceeding with loan test");

        return loginPage;
    }

    // ═══════════════════════════════════════════════════════════
    //  POSITIVE TEST CASES
    // ═══════════════════════════════════════════════════════════
    @Test(priority = 1,
          groups = {"smoke", "regression"},
          description = "TC-LOAN-01 — Valid loan application should be approved")
    public void TC01_validLoanApplication() {

        LoginPage loginPage = loginWithExcelCredentials();

        // ── Step 1: Record balance before applying ───────────────────
        log.info("Navigating to Accounts Overview to record balance before loan application");
        ExtentReportsManager.getTest()
            .info("📊 Navigating to Accounts Overview");

        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();

        String sourceAccount  = overviewPage.getFirstAccountNumber();
        double balanceBefore  = overviewPage.getAccountBalance(sourceAccount);

        log.info("Source Account: {} | Balance Before: ${}", sourceAccount, balanceBefore);
        ExtentReportsManager.getTest()
            .info("💰 Loan source details:"
                + "<br>&nbsp;&nbsp;Source Account : " + sourceAccount
                + "<br>&nbsp;&nbsp;Balance Before : $" + balanceBefore);

        // ── Step 2: Apply for loan ────────────────────────────────────
        log.info("Navigating to Request Loan page");
        ExtentReportsManager.getTest()
            .info("🌐 Navigating to Request Loan page"
                + "<br>&nbsp;&nbsp;Loan Amount   : $1000"
                + "<br>&nbsp;&nbsp;Down Payment  : $100"
                + "<br>&nbsp;&nbsp;From Account  : " + sourceAccount);

        RequestLoanPage loanPage = new RequestLoanPage(getDriver());
        loanPage.navigateToRequestLoan();
        loanPage.enterLoanAmount("1000");
        loanPage.enterDownPayment("100");
        loanPage.selectFromAccount(sourceAccount);
        loanPage.clickApplyNow();

        log.info("Loan application form submitted");
        ExtentReportsManager.getTest()
            .info("📤 Loan application form submitted");

        // ── Step 3: Verify result shown ───────────────────────────────
        Assert.assertTrue(
            loanPage.isLoanResultDisplayed(),
            "Loan result not displayed after applying"
        );

        log.info("Loan result displayed ✅");
        ExtentReportsManager.getTest()
            .info("✅ Loan result displayed after submission");

        String status   = loanPage.getLoanStatus();
        String provider = loanPage.getLoanProviderName();

        log.info("Loan Status: {} | Loan Provider: {}", status, provider);
        ExtentReportsManager.getTest()
            .info("🔍 Loan application result:"
                + "<br>&nbsp;&nbsp;Loan Status   : " + status
                + "<br>&nbsp;&nbsp;Loan Provider : " + provider);

        // Hard assert — loan must be approved for subsequent checks
        Assert.assertTrue(
            loanPage.isLoanApproved(),
            "Loan was NOT approved. Status: " + status
        );

        log.info("Loan approved ✅");
        ExtentReportsManager.getTest()
            .info("✅ Loan approved — Status: " + status);

        // ── Step 4: Get and verify new loan account ───────────────────
        String newLoanAccount = loanPage.getNewLoanAccountNumber();
        log.info("New Loan Account: {}", newLoanAccount);
        ExtentReportsManager.getTest()
            .info("🆕 New Loan Account generated: " + newLoanAccount);

        Assert.assertFalse(
            newLoanAccount.isEmpty(),
            "No new account number shown after loan approval"
        );

        log.info("New loan account number verified — not empty ✅");
        ExtentReportsManager.getTest()
            .info("✅ New loan account number is present (non-empty)");

        // ── Step 5: Verify new account visible in overview ───────────
        log.info("Navigating to Accounts Overview to verify new loan account visibility");
        ExtentReportsManager.getTest()
            .info("📊 Navigating to Accounts Overview to verify new loan account: "
                + newLoanAccount);

        overviewPage.navigateToOverview();

        SoftAssert softAssert = new SoftAssert();

        boolean newAccountFound = overviewPage.findAccountNumber(newLoanAccount);

        log.info("New loan account found in overview: {}", newAccountFound);
        ExtentReportsManager.getTest()
            .info("🔍 Verifying new loan account " + newLoanAccount
                + " is visible in Accounts Overview — Found: " + newAccountFound);

        softAssert.assertTrue(
            newAccountFound,
            "New loan account " + newLoanAccount
            + " not visible in Accounts Overview"
        );

        if (newAccountFound) {
            log.info("New loan account visible in overview ✅");
            ExtentReportsManager.getTest()
                .info("✅ New loan account " + newLoanAccount
                    + " is visible in Accounts Overview");
        } else {
            log.error("DEFECT — New loan account {} not visible in overview ❌", newLoanAccount);
            ExtentReportsManager.getTest()
                .fail("❌ DEFECT — New loan account " + newLoanAccount
                    + " not visible in Accounts Overview");
        }

        // ── Step 6: Verify source account reduced by down payment ─────
        double balanceAfter   = overviewPage.getAccountBalance(sourceAccount);
        double expectedBalance = balanceBefore - 100.0;

        log.info("Balance After: ${} | Expected After: ${}", balanceAfter, expectedBalance);
        ExtentReportsManager.getTest()
            .info("💰 Source account balance verification:"
                + "<br>&nbsp;&nbsp;Balance Before  : $" + balanceBefore
                + "<br>&nbsp;&nbsp;Down Payment    : $100"
                + "<br>&nbsp;&nbsp;Balance After   : $" + balanceAfter
                + "<br>&nbsp;&nbsp;Expected After  : $" + expectedBalance);

        softAssert.assertEquals(
            balanceAfter,
            expectedBalance,
            0.01,
            "Source account not reduced by down payment amount. " +
            "Expected: " + expectedBalance +
            " Actual: " + balanceAfter
        );

        if (Math.abs(balanceAfter - expectedBalance) <= 0.01) {
            log.info("Source account correctly reduced by down payment ✅");
            ExtentReportsManager.getTest()
                .info("✅ Source account correctly reduced by down payment amount ($100)");
        } else {
            log.error("DEFECT — Source account balance mismatch. Expected: {} Actual: {} ❌",
                expectedBalance, balanceAfter);
            ExtentReportsManager.getTest()
                .fail("❌ DEFECT — Source account not reduced correctly. Expected: $"
                    + expectedBalance + " Actual: $" + balanceAfter);
        }

        softAssert.assertAll();

        log.info("TC-LOAN-01 — All loan verifications passed ✅");
        ExtentReportsManager.getTest()
            .info("✅ TC-LOAN-01 Complete — All loan verifications passed!");

        // ── Step 7: Logout ─────────────────────────────────────────────
        log.info("Logging out after loan verification");
        ExtentReportsManager.getTest()
            .info("🚪 Logging out — TC-LOAN-01 complete");

        loginPage.logout();

        log.info("TC01_validLoanApplication completed ✅");
        ExtentReportsManager.getTest()
            .info("✅ TC01_validLoanApplication completed successfully");
    }
}