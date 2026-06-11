package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.BillPaymentPage;
import pages.LoginPage;
import utilities.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * BillPaymentTest covers all critical bill payment scenarios.
 *
 * CONCEPT — What makes a defect "critical" in banking:
 * We focus on defects that directly affect money or security:
 *   1. Payment succeeds with mismatched account numbers
 *   2. Payment succeeds with empty/invalid amount
 *   3. Balance not deducted after successful payment
 *   4. No transaction record created after payment
 *   5. Payment succeeds with empty required fields
 *
 * Non-critical defects (UI alignment, label typos) are
 * noted but not automated — they don't affect functionality.
 */
public class BillPaymentTest extends BaseTest {

    private static final String LOGIN_SHEET = "LoginData";

    // ── Reusable test data constants ─────────────────────────────
    private static final String PAYEE_NAME    = "Shubhank";
    private static final String PAYEE_ADDRESS = "123 Test Street";
    private static final String PAYEE_CITY    = "New York";
    private static final String PAYEE_STATE   = "NY";
    private static final String PAYEE_ZIP     = "10001";
    private static final String PAYEE_PHONE   = "9876543210";
    private static final String PAYEE_ACCOUNT = "13122"; // existing account
    private static final String PAY_AMOUNT    = "100";

    // ─────────────────────────────────────────────
    //  HELPER — Login
    // ─────────────────────────────────────────────

    private LoginPage loginWithExcelCredentials() {
        String username = ExcelUtils.getCellData(LOGIN_SHEET, 1, 0);
        String password = ExcelUtils.getCellData(LOGIN_SHEET, 1, 1);

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);

        Assert.assertTrue(
            loginPage.isLoginSuccessful(),
            "Login failed in BillPaymentTest setup"
        );
        System.out.println("Login successful — proceeding with Bill Payment test");
        return loginPage;
    }

    // ─────────────────────────────────────────────
    //  TC01 — Valid Bill Payment + Balance Verification
    // ─────────────────────────────────────────────

    /**
     * The most critical test — verifies the complete payment flow:
     * 1. Record balance BEFORE payment
     * 2. Make payment
     * 3. Verify success message shows correct payee and amount
     * 4. Go to Account Overview → verify balance REDUCED
     * 5. Click account → verify "Bill Payment to X" debit in activity
     */
    @Test(priority = 1,
          description = "TC01 - Valid bill payment and verify balance deduction")
    public void testValidBillPaymentWithBalanceVerification() {

        LoginPage loginPage = loginWithExcelCredentials();

        // ── Step 1: Get FROM account and record balance BEFORE ───────
        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();

        // Use first account as FROM account for payment
        String fromAccount = overviewPage.getFirstAccountNumber();
        double balanceBefore = overviewPage.getAccountBalance(fromAccount);

        System.out.println("\n════════════ PAYMENT SOURCE DETAILS ════════════");
        System.out.println("Account Number : " + fromAccount);
        System.out.println("Balance : $" + balanceBefore);
        System.out.println("════════════════════════════════════════════════");

        // ── Step 2: Make the bill payment ────────────────────────────
        BillPaymentPage billPayPage = new BillPaymentPage(getDriver());
        billPayPage.navigateToBillPay();
        billPayPage.fillAndSubmitBillPayment(
            PAYEE_NAME, PAYEE_ADDRESS, PAYEE_CITY, PAYEE_STATE,
            PAYEE_ZIP, PAYEE_PHONE, PAYEE_ACCOUNT, PAY_AMOUNT, fromAccount
        );

        // Hard assert — if payment failed entirely, stop here
        Assert.assertTrue(
            billPayPage.isPaymentSuccessful(),
            "Bill payment failed — success message not shown"
        );

        // ── Step 3: Verify success message content ───────────────────

        /**
         * CONCEPT — Verifying success message content:
         * Just checking the div appeared is not enough.
         * We also verify the CORRECT payee name and amount
         * are shown — this catches bugs where wrong data
         * is displayed in the confirmation message.
         */
        SoftAssert softAssert = new SoftAssert();

        String resultPayee  = billPayPage.getResultPayeeName();
        String resultAmount = billPayPage.getResultAmount();
        String resultAcct   = billPayPage.getResultAccountId();

        System.out.println("\n════════════ PAYMENT CONFIRMATION ════════════");
        System.out.println("Payee   : " + resultPayee);
        System.out.println("Amount  : " + resultAmount);
        System.out.println("Account : " + resultAcct);
        System.out.println("══════════════════════════════════════════════");

        softAssert.assertEquals(
            resultPayee, PAYEE_NAME,
            "DEFECT — Success message shows wrong payee name. " +
            "Expected: " + PAYEE_NAME + " Got: " + resultPayee
        );

        softAssert.assertTrue(
            resultAmount.contains(PAY_AMOUNT),
            "DEFECT — Success message shows wrong amount. " +
            "Expected to contain: " + PAY_AMOUNT + " Got: " + resultAmount
        );

        softAssert.assertEquals(
            resultAcct, fromAccount,
            "DEFECT — Success message shows wrong account. " +
            "Expected: " + fromAccount + " Got: " + resultAcct
        );
        
        System.out.println("\n-------------- ACCOUNT ACTIVITY --------------");
        // ── Step 4: Verify balance REDUCED in Account Overview ───────
        overviewPage.navigateToOverview();
        double balanceAfter = overviewPage.getAccountBalance(fromAccount);

        System.out.println("Balance AFTER payment: " + balanceAfter);

        double expectedBalance = balanceBefore - Double.parseDouble(PAY_AMOUNT);

        softAssert.assertEquals(
            balanceAfter, expectedBalance, 0.01,
            "CRITICAL DEFECT — Balance not reduced after bill payment! " +
            "Expected: " + expectedBalance + " Actual: " + balanceAfter
        );

        // ── Step 5: Verify transaction in account activity ───────────
        overviewPage.clickAccountNumber(fromAccount);

        /**
         * Bill payment shows as "Bill Payment to <PayeeName>" in activity.
         * We check for the payee name in the transaction description.
         */
        boolean transactionFound = overviewPage.isBillPaymentTransactionPresent(PAYEE_NAME);

        softAssert.assertTrue(
            transactionFound,
            "CRITICAL DEFECT — Bill Payment transaction not found in account activity! " +
            "Expected transaction: 'Bill Payment to " + PAYEE_NAME + "'"
        );

        softAssert.assertAll();
        System.out.println("TC01 — All bill payment verifications passed");

        loginPage.logout();
    }
}
    // ─────────────────────────────────────────────
    //  TC02 — Mismatched Account Numbers (Critical Defect)
    // ─────────────────────────────────────────────
    // ─────────────────────────────────────────────
    //  TC03 — Empty Amount Field (Critical Defect)
    // ─────────────────────────────────────────────
    // ─────────────────────────────────────────────
    //  TC04 — Invalid Amount (Letters) (Critical Defect)
    // ─────────────────────────────────────────────
    // ─────────────────────────────────────────────
    //  TC05 — Empty Payee Name (Critical Defect)
    // ─────────────────────────────────────────────