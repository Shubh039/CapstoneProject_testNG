package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.LoginPage;
import pages.RequestLoanPage;
import utilities.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class RequestLoanTest extends BaseTest {

    private static final String LOGIN_SHEET = "LoginData";

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
            "Login failed in RequestLoanTest"
        );
        System.out.println("Login successful — proceeding with loan test");
        return loginPage;
    }

    // ═══════════════════════════════════════════════════════════
    //  POSITIVE TEST CASES
    // ═══════════════════════════════════════════════════════════
    @Test(priority = 1,
          description = "TC-LOAN-01 — Valid loan application should be approved")
    public void TC01_validLoanApplication() {

        LoginPage loginPage = loginWithExcelCredentials();

        // ── Step 1: Record balance before applying ───────────────────
        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();

        String sourceAccount  = overviewPage.getFirstAccountNumber();
        double balanceBefore  = overviewPage.getAccountBalance(sourceAccount);

        System.out.println("\n════════════ LOAN APPLICATION DETAILS ════════════");
        System.out.println("Source Account : " + sourceAccount);
        System.out.println("Balance Before : $" + balanceBefore);

        // ── Step 2: Apply for loan ────────────────────────────────────
        RequestLoanPage loanPage = new RequestLoanPage(getDriver());
        loanPage.navigateToRequestLoan();
        loanPage.enterLoanAmount("1000");
        loanPage.enterDownPayment("100");
        loanPage.selectFromAccount(sourceAccount);
        loanPage.clickApplyNow();

        // ── Step 3: Verify result shown ───────────────────────────────
        Assert.assertTrue(
            loanPage.isLoanResultDisplayed(),
            "Loan result not displayed after applying"
        );

        String status   = loanPage.getLoanStatus();
        String provider = loanPage.getLoanProviderName();

        System.out.println("Loan Status   : " + status);
        System.out.println("Loan Provider : " + provider);
        System.out.println("══════════════════════════════════════════════════");

        // Hard assert — loan must be approved for subsequent checks
        Assert.assertTrue(
            loanPage.isLoanApproved(),
            "Loan was NOT approved. Status: " + status
        );

        // ── Step 4: Get and verify new loan account ───────────────────
        String newLoanAccount = loanPage.getNewLoanAccountNumber();
        System.out.println("New Loan Account : " + newLoanAccount);

        Assert.assertFalse(
            newLoanAccount.isEmpty(),
            "No new account number shown after loan approval"
        );

        // ── Step 5: Verify new account visible in overview ───────────
        overviewPage.navigateToOverview();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
            overviewPage.findAccountNumber(newLoanAccount),
            "New loan account " + newLoanAccount
            + " not visible in Accounts Overview"
        );

        // ── Step 6: Verify source account reduced by down payment ─────
        double balanceAfter = overviewPage.getAccountBalance(sourceAccount);
        System.out.println("Balance After  : $" + balanceAfter);
        System.out.println("Expected After : $" + (balanceBefore - 100.0));

        softAssert.assertEquals(
            balanceAfter,
            balanceBefore - 100.0,
            0.01,
            "Source account not reduced by down payment amount. " +
            "Expected: " + (balanceBefore - 100.0) +
            " Actual: " + balanceAfter
        );

        softAssert.assertAll();

        System.out.println("TC-LOAN-01 — All loan verifications passed ✅");
        loginPage.logout();
    }

//    // ═══════════════════════════════════════════════════════════
//    //  NEGATIVE TEST CASES — DEFECT HUNT
//    // ═══════════════════════════════════════════════════════════
//
//    /**
//     * TC-LOAN-03 — DEFECT: Negative down payment accepted.
//     *
//     * From manual testing:
//     * Down payment of -10 is accepted.
//     * This effectively ADDS money to account instead of deducting.
//     * e.g. Account has $0, down payment -10 → balance becomes -90
//     * instead of being rejected.
//     *
//     * Expected: System should reject negative down payment.
//     * Defect: Loan approved with negative down payment.
//     */
//    @Test(priority = 3,
//          description = "TC-LOAN-03 — Negative down payment should be rejected")
//    public void TC03_negativeDownPaymentDefect() {
//
//        LoginPage loginPage = loginWithExcelCredentials();
//
//        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
//        overviewPage.navigateToOverview();
//
//        String sourceAccount = overviewPage.getFirstAccountNumber();
//        double balanceBefore = overviewPage.getAccountBalance(sourceAccount);
//
//        System.out.println("\n════════ DEFECT TEST — NEGATIVE DOWN PAYMENT ════════");
//        System.out.println("Source Account  : " + sourceAccount);
//        System.out.println("Balance Before  : $" + balanceBefore);
//        System.out.println("Down Payment    : -10 (negative — should be rejected)");
//
//        RequestLoanPage loanPage = new RequestLoanPage(getDriver());
//        loanPage.navigateToRequestLoan();
//        loanPage.enterLoanAmount("1000");
//        loanPage.enterDownPayment("-10");   // Negative down payment
//        loanPage.selectFromAccount(sourceAccount);
//        loanPage.clickApplyNow();
//
//        boolean resultShown    = loanPage.isLoanResultDisplayed();
//        boolean loanApproved   = loanPage.isLoanApproved();
//        boolean serverError    = loanPage.isServerErrorDisplayed();
//
//        System.out.println("Result Shown    : " + resultShown);
//        System.out.println("Loan Approved   : " + loanApproved);
//        System.out.println("Server Error    : " + serverError);
//
//        // Check balance after to document the impact
//        if (loanApproved) {
//            overviewPage.navigateToOverview();
//            double balanceAfter = overviewPage.getAccountBalance(sourceAccount);
//            System.out.println("Balance After   : $" + balanceAfter);
//            System.out.println("Balance Change  : $" + (balanceAfter - balanceBefore));
//            System.out.println(
//                "DEFECT IMPACT   : Negative down payment ADDED $10 to account " +
//                "instead of being rejected!"
//            );
//        }
//        System.out.println("═════════════════════════════════════════════════════");
//
//        /**
//         * We assert the loan should NOT be approved with -10 down payment.
//         * If it IS approved → defect confirmed → test documents it.
//         */
//        SoftAssert softAssert = new SoftAssert();
//        softAssert.assertFalse(
//            loanApproved,
//            "DEFECT FOUND — Loan approved with negative down payment (-10)! " +
//            "System should reject negative down payment values."
//        );
//        softAssert.assertAll();
//
//        loginPage.logout();
//    }
//
//    /**
//     * TC-LOAN-04 — DEFECT: Account balance goes negative due to down payment.
//     *
//     * From manual testing:
//     * Account with $0 balance + down payment of $1000
//     * → Loan approved → account balance becomes -$1000
//     * A bank should NEVER allow account to go below $0.
//     *
//     * Expected: Loan should be denied — insufficient funds for down payment.
//     * Defect: Loan approved, account goes negative.
//     */
//    @Test(priority = 4,
//          description = "TC-LOAN-04 — Loan with down payment exceeding balance should be rejected")
//    public void TC04_downPaymentExceedsBalanceDefect() {
//
//        LoginPage loginPage = loginWithExcelCredentials();
//
//        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
//        overviewPage.navigateToOverview();
//
//        // Find an account with low or zero balance
//        // We use last account which typically has lowest balance
//        String sourceAccount = overviewPage.getFirstAccountNumber();
//        double balanceBefore = overviewPage.getAccountBalance(sourceAccount);
//
//        System.out.println("\n════════ DEFECT TEST — BALANCE GOES NEGATIVE ════════");
//        System.out.println("Source Account  : " + sourceAccount);
//        System.out.println("Balance Before  : $" + balanceBefore);
//        System.out.println("Down Payment    : $5000 (exceeds balance)");
//
//        RequestLoanPage loanPage = new RequestLoanPage(getDriver());
//        loanPage.navigateToRequestLoan();
//        loanPage.enterLoanAmount("1000");
//        loanPage.enterDownPayment("5000");  // Down payment > account balance
//        loanPage.selectFromAccount(sourceAccount);
//        loanPage.clickApplyNow();
//
//        boolean resultShown  = loanPage.isLoanResultDisplayed();
//        boolean loanApproved = loanPage.isLoanApproved();
//        boolean loanDenied   = loanPage.isLoanDenied();
//        String  status       = loanPage.getLoanStatus();
//
//        System.out.println("Result Shown    : " + resultShown);
//        System.out.println("Loan Status     : " + status);
//
//        // Check balance impact
//        overviewPage.navigateToOverview();
//        double balanceAfter = overviewPage.getAccountBalance(sourceAccount);
//
//        System.out.println("Balance After   : $" + balanceAfter);
//
//        boolean balanceWentNegative = balanceAfter < 0;
//        System.out.println("Balance Negative: " + balanceWentNegative);
//
//        if (balanceWentNegative) {
//            System.out.println(
//                "DEFECT IMPACT   : Account balance went to $" + balanceAfter +
//                " — banking systems must not allow negative balances!"
//            );
//        }
//        System.out.println("═════════════════════════════════════════════════════");
//
//        SoftAssert softAssert = new SoftAssert();
//
//        softAssert.assertFalse(
//            balanceWentNegative,
//            "CRITICAL DEFECT — Account balance went negative ($" + balanceAfter + ") " +
//            "after loan with down payment exceeding available funds!"
//        );
//
//        softAssert.assertAll();
//
//        loginPage.logout();
//    }
//
//    /**
//     * TC-LOAN-05 — Letters in loan amount cause server error.
//     *
//     * From manual testing: Letters in amount field cause
//     * "An internal error has occurred and has been logged"
//     * This is a poor user experience — should show a proper
//     * validation message instead of a server error.
//     *
//     * Expected: Client-side validation error shown.
//     * Actual: Server-side error (poor UX but not critical).
//     */
//    @Test(priority = 5,
//          description = "TC-LOAN-05 — Letters in loan amount should show validation error")
//    public void TC05_lettersInLoanAmountCauseServerError() {
//
//        LoginPage loginPage = loginWithExcelCredentials();
//
//        RequestLoanPage loanPage = new RequestLoanPage(getDriver());
//        loanPage.navigateToRequestLoan();
//        loanPage.enterLoanAmount("abc");    // Letters — invalid input
//        loanPage.enterDownPayment("100");
//        loanPage.selectFromAccountByIndex(0);
//        loanPage.clickApplyNow();
//
//        boolean serverError  = loanPage.isServerErrorDisplayed();
//        boolean resultShown  = loanPage.isLoanResultDisplayed();
//
//        System.out.println("\n════════ DEFECT TEST — LETTERS IN AMOUNT FIELD ════════");
//        System.out.println("Loan Amount  : abc (letters)");
//        System.out.println("Server Error : " + serverError);
//        System.out.println("Result Shown : " + resultShown);
//        System.out.println(
//            serverError
//            ? "DEFECT — Server error shown instead of client validation message"
//            : "OK — Proper validation shown"
//        );
//        System.out.println("═══════════════════════════════════════════════════════");
//
//        /**
//         * We assert that the loan should NOT be approved.
//         * Whether it shows server error or validation error,
//         * the key thing is the loan was NOT processed.
//         */
//        Assert.assertFalse(
//            loanPage.isLoanApproved(),
//            "Loan should not be approved with letters as amount"
//        );
//
//        System.out.println("TC-LOAN-05 — Letters in amount correctly blocked loan ✅");
//        loginPage.logout();
//    }
}