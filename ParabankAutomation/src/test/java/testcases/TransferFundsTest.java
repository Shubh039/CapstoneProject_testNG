package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.LoginPage;
import pages.TransferFundsPage;
import utilities.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class TransferFundsTest extends BaseTest {

    private static final String LOGIN_SHEET    = "LoginData";
    private static final String TRANSFER_AMOUNT = "100";

    // ─────────────────────────────────────────────
    //  HELPER — Login
    // ─────────────────────────────────────────────

    /**
     * Reusable login helper so we don't repeat login code
     * in every test method.
     *
     * CONCEPT — Private helper methods in test classes:
     * Common setup steps shared by multiple tests in the SAME
     * class go here as private methods. If shared across
     * multiple test classes, they would go in BaseTest or
     * a utility class.
     */
    private void loginWithExcelCredentials() {
        String username = ExcelUtils.getCellData(LOGIN_SHEET, 1, 0);
        String password = ExcelUtils.getCellData(LOGIN_SHEET, 1, 1);

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        Assert.assertTrue(
            loginPage.isLoginSuccessful(),
            "Login failed in TransferFundsTest setup"
        );
        System.out.println("Login successful — proceeding with transfer test");
    }

    // ─────────────────────────────────────────────
    //  TC01 — Valid Transfer with Balance Verification
    // ─────────────────────────────────────────────

    /**
     * This is the most important test — it:
     * 1. Reads balances BEFORE transfer
     * 2. Performs transfer
     * 3. Reads balances AFTER transfer
     * 4. Verifies FROM account decreased by transfer amount
     * 5. Verifies TO account increased by transfer amount
     * 6. Checks transaction record exists in account activity
     */
    @Test(priority = 1,
          description = "TC01 - Valid transfer and verify balance changes")
    public void testValidTransferWithBalanceVerification() {

        loginWithExcelCredentials();

        // ── Step 1: Get account numbers from overview ────────────────
        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();

        String fromAccount = overviewPage.getFirstAccountNumber();
        String toAccount   = overviewPage.getSecondAccountNumber();
        
        // ── Step 2: Record balances BEFORE transfer ──────────────────
        double fromBalanceBefore = overviewPage.getAccountBalance(fromAccount);
        double toBalanceBefore   = overviewPage.getAccountBalance(toAccount);

        System.out.println("\n══════════════ FUND TRANSFER DETAILS ══════════════");
        System.out.println("FROM Account : " + fromAccount+ " | Balance : $" + fromBalanceBefore);
        System.out.println("TO Account   : " + toAccount+ " | Balance : $" + toBalanceBefore);
        System.out.println("═══════════════════════════════════════════════════");

        // ── Step 3: Perform the transfer ─────────────────────────────
        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        transferPage.transferFunds(TRANSFER_AMOUNT, fromAccount, toAccount);

        // Hard assert — if transfer didn't succeed, no point checking balances
        Assert.assertTrue(
            transferPage.isTransferSuccessful(),
            "Transfer failed — success message not shown"
        );
        System.out.println("\n-------------- ACCOUNT ACTIVITY --------------");
        // ── Step 4: Record balances AFTER transfer ───────────────────
        overviewPage.navigateToOverview();

        double fromBalanceAfter = overviewPage.getAccountBalance(fromAccount);
        double toBalanceAfter   = overviewPage.getAccountBalance(toAccount);

        System.out.println("\n══════════════ UPDATED BALANCES ══════════════");
        System.out.println("FROM Account : " + fromAccount+ " | Balance : $" + fromBalanceAfter);
        System.out.println("TO Account   : " + toAccount+ " | Balance : $" + toBalanceAfter);
        System.out.println("══════════════════════════════════════════════");

        double transferAmount = Double.parseDouble(TRANSFER_AMOUNT);

        // ── Step 5: Verify balances using SoftAssert ─────────────────

        /**
         * CONCEPT — Why delta in assertEquals for doubles:
         * Floating point arithmetic is imprecise.
         * 100.00 - 50.00 might give 49.999999999 not 50.0
         * The delta (0.01) means "accept values within 0.01 of expected"
         * This prevents false failures due to floating point issues.
         */
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
            fromBalanceAfter,
            fromBalanceBefore - transferAmount,
            0.01,
            "FROM account balance not reduced correctly. " +
            "Expected: " + (fromBalanceBefore - transferAmount) +
            " Actual: " + fromBalanceAfter
        );

        softAssert.assertEquals(
            toBalanceAfter,
            toBalanceBefore + transferAmount,
            0.01,
            "TO account balance not increased correctly. " +
            "Expected: " + (toBalanceBefore + transferAmount) +
            " Actual: " + toBalanceAfter
        );

        // ── Step 6: Verify transaction in account activity ───────────
        overviewPage.clickAccountNumber(fromAccount);
        boolean debitFound = overviewPage.isDebitTransactionPresent(TRANSFER_AMOUNT);

        softAssert.assertTrue(
            debitFound,
            "Debit transaction not found in FROM account activity"
        );

        // Go back to overview to check TO account
        overviewPage.navigateToOverview();
        overviewPage.clickAccountNumber(toAccount);
        boolean creditFound = overviewPage.isCreditTransactionPresent(TRANSFER_AMOUNT);

        softAssert.assertTrue(
            creditFound,
            "Credit transaction not found in TO account activity"
        );

        // CRITICAL — must call assertAll() to trigger any soft failures
        softAssert.assertAll();

        System.out.println("\nAll balance and transaction verifications passed ✅");

        new LoginPage(getDriver()).logout();
    }

    // ─────────────────────────────────────────────
    //  TC02 — Transfer with Zero Amount (Defect Hunt)
    // ─────────────────────────────────────────────

    // ─────────────────────────────────────────────
    //  TC03 — Transfer with Negative Amount (Defect Hunt)
    // ─────────────────────────────────────────────

    // ─────────────────────────────────────────────
    //  TC04 — Transfer to Same Account (Defect Hunt)
    // ─────────────────────────────────────────────
}