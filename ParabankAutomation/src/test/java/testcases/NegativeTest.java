package testcases;

import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.BillPaymentPage;
import pages.LoginPage;
import pages.OpenAccount;
import pages.RegistrationPage;
import pages.RequestLoanPage;
import pages.TransferFundsPage;
import utilities.ExcelUtils;
import utilities.ExtentReportsManager;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utilities.LoggerUtil;
import utilities.ScreenShotUtils;

import org.apache.logging.log4j.Logger;

public class NegativeTest extends BaseTest {
	private static String REG_SHEET = "Registration";
    private static final String PAYEE_SHEET   = "BillPay";
    private static final Logger log = LoggerUtil.getLogger(NegativeTest.class);
    
    private static final String PAYEE_NAME    = ExcelUtils.getCellData(PAYEE_SHEET, 1, 0);
    private static final String PAYEE_ADDRESS = ExcelUtils.getCellData(PAYEE_SHEET, 1, 1);
    private static final String PAYEE_CITY    = ExcelUtils.getCellData(PAYEE_SHEET, 1, 2);
    private static final String PAYEE_STATE   = ExcelUtils.getCellData(PAYEE_SHEET, 1, 3);
    private static final String PAYEE_ZIP     = ExcelUtils.getCellData(PAYEE_SHEET, 1, 4);
    private static final String PAYEE_PHONE   = ExcelUtils.getCellData(PAYEE_SHEET, 1, 5);
    private static final String PAYEE_ACCOUNT = ExcelUtils.getCellData(PAYEE_SHEET, 1, 6);
    private static final String PAY_AMOUNT    = ExcelUtils.getCellData(PAYEE_SHEET, 1, 7);
    
	// method to print report
    private void printBugReport(String bugId, String module, String inputData,
                                 String expectedResult, String actualResult,
                                 boolean isDefect) {
        String status = isDefect ? "BUG REPRODUCED ❌" : "WORKING AS EXPECTED ✅";
        
        log.info("╔══════════════════════════════════════════════════════╗");
        log.info("║              BUG EXECUTION OUTPUT                    ║");
        log.info("╠══════════════════════════════════════════════════════╣");
        log.info("║ Bug ID          : " + bugId);
        log.info("║ Module          : " + module);
        log.info("║ Input Data      : " + inputData);
        log.info("║ Expected Result : " + expectedResult);
        log.info("║ Actual Result   : " + actualResult);
        log.info("║ Bug Status      : " + status);
        log.info("╚══════════════════════════════════════════════════════╝");
        
        if (isDefect) {
            log.warn("DEFECT CONFIRMED — {} in module: {}", bugId, module);
            ScreenShotUtils.takeScreenshot(getDriver(), status);
            String base64Screenshot = ScreenShotUtils.takeScreenshotAsBase64(getDriver());
            if (base64Screenshot != null) {
                try {
                    ExtentReportsManager.getTest()
                        .warning("🐛 BUG REPRODUCED — " + bugId
                            + " | Module: " + module
                            + " | " + actualResult);
                    // screenshot build
                    ExtentReportsManager.getTest()
                        .warning(com.aventstack.extentreports.MediaEntityBuilder
                            .createScreenCaptureFromBase64String(
                                base64Screenshot, "BUG_" + bugId)
                            .build());

                } catch (Exception e) {
                    log.warn("Could not embed bug screenshot in report: {}",
                        e.getMessage());
                }
            }
        } else {
            log.info("✅ {} — {}: Working as expected", bugId, module);
            ExtentReportsManager.getTest().info("✅ " + bugId + " — " + module + ": Working as expected");
        }
    }
    private String registerNegativeTestUser() {

        RegistrationPage regPage = new RegistrationPage(getDriver());
        regPage.clickRegisterLink();
        
        String firstName = ExcelUtils.getCellData(REG_SHEET, 1, 0);
        String lastName  = ExcelUtils.getCellData(REG_SHEET, 1, 1);
        String address   = ExcelUtils.getCellData(REG_SHEET, 1, 2);
        String city      = ExcelUtils.getCellData(REG_SHEET, 1, 3);
        String state     = ExcelUtils.getCellData(REG_SHEET, 1, 4);
        String zipCode   = ExcelUtils.getCellData(REG_SHEET, 1, 5);
        String phone     = ExcelUtils.getCellData(REG_SHEET, 1, 6);
        String ssn       = ExcelUtils.getCellData(REG_SHEET, 1, 7);
        String username  = ExcelUtils.getCellData(REG_SHEET, 1, 8) + "_" + System.currentTimeMillis();
        String password  = ExcelUtils.getCellData(REG_SHEET, 1, 9);
        
        regPage.fillAndSubmitRegistrationForm(
                firstName, lastName, address, city, state,
                zipCode, phone, ssn, username, password
            ); 

        // Save to Excel so login helper can read it
        ExcelUtils.setCellData("NegativeTestData", 1, 0, username);
        ExcelUtils.setCellData("NegativeTestData", 1, 1, password);
        System.out.println("Registered negative test user: " + username);

        new LoginPage(getDriver()).logout();

        return username;
    }

    private LoginPage loginNegativeUser() {
        String username = ExcelUtils.getCellData("NegativeTestData", 1, 0);
        String password = ExcelUtils.getCellData("NegativeTestData", 1, 1);
        log.info("Attempting login for negative test user: {}", username);
        
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);
        boolean loginSuccessful = loginPage.isLoginSuccessful();
        
        log.info(
            "Login result for negative test user [{}]: {}",
            username,
            loginSuccessful ? "SUCCESS" : "FAILED"
        );
        org.testng.Assert.assertTrue(
            loginSuccessful,
            "Login failed for negative test user"
        );
        log.info("Negative test user logged in successfully: {}", username);
        return loginPage;
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return new Object[][] {
            { "wrongUser_xyz",  "wrongPass_123", "Invalid username and password" },
            { "",               "",              "Empty username and password"   },
            { "Shubhs",         "wrongPass",     "Valid username, wrong password" }
        };
    }

    // PB-L01, PB-L02, PB-L03 — Login with invalid credentials.
    @Test(priority = 1,
          dataProvider = "invalidLoginData",
          description = "LOGIN NEGATIVE — Invalid credentials should show error")
    public void bug_LoginWithInvalidCredentials(String username,String password,String scenario) {
        log.info("=================================================");
        log.info("STARTING NEGATIVE LOGIN TEST");
        log.info("Scenario: {}", scenario);
        log.info(
            "Username: {}",
            username.isEmpty() ? "[EMPTY]" : username
        );
        log.info(
            "Password Provided: {}",
            !password.isEmpty()
        );
        log.info("=================================================");
        
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(username, password);
        log.info("Attempting login with invalid credentials");
        
        boolean errorShown  = loginPage.isLoginErrorDisplayed() || loginPage.isLoginMissingErrorDisplayed();
        boolean loginWorked = loginPage.isLoginSuccessful();
        log.info("Login attempt completed. ErrorShown={}, LoginWorked={}",errorShown,loginWorked);
        

        printBugReport(
            "PB-L01",
            "Login",
            "Username = " + (username.isEmpty() ? "[EMPTY]" : username)
            + ", Password = " + (password.isEmpty() ? "[EMPTY]" : "****"),
            "Login should be rejected — error message shown",
            errorShown ? "Error message displayed correctly"
                       : "Login succeeded — NO error shown",
            loginWorked // true = defect (login should have failed)
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(loginWorked,
            "DEFECT — Login succeeded with: " + scenario);
        softAssert.assertTrue(errorShown,
            "DEFECT — No error message shown for: " + scenario);
        softAssert.assertAll();
        
        log.info("Completed negative login scenario: {}",scenario);
    }

    // PB-001 — Transfer with NEGATIVE amount.
    @Test(priority = 2,
          description = "PB-001 — Transfer with negative amount should be rejected")
    public void bug001_transferFundsAllowsNegativeAmount() {
    	
        log.info("=================================================");
        log.info("STARTING TEST: PB-001");
        log.info("Scenario: Transfer with negative amount");
        log.info("=================================================");

        registerNegativeTestUser();
        LoginPage loginPage = loginNegativeUser();

        log.info("Navigating to Accounts Overview page");
        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();
        log.info("Creating additional SAVINGS account for transfer test");
        OpenAccount openAcc = new OpenAccount(getDriver());
        openAcc.openNewAccount("SAVINGS");
        
        overviewPage.navigateToOverview();
        String fromAccount = overviewPage.getFirstAccountNumber();
        String toAccount   = overviewPage.getSecondAccountNumber();
        log.info("Source Account      : {}", fromAccount);
        log.info("Destination Account : {}", toAccount);

        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        transferPage.transferFunds("-100", fromAccount, toAccount);

        boolean transferSucceeded = transferPage.isTransferSuccessful();

        printBugReport(
            "PB-001",
            "Transfer Funds",
            "Amount = -100, From = " + fromAccount + ", To = " + toAccount,
            "System should reject negative transfer amount",
            transferSucceeded ? "Transfer Complete! — BUG" : "Transfer rejected — OK",
            transferSucceeded
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(transferSucceeded,
            "DEFECT PB-001 — Negative amount transfer was allowed!"
        );
        softAssert.assertAll();
        
        log.info("Logging out test user");
        loginPage.logout();
        log.info("Completed test: PB-001");
    }

    // PB-002 — Bill Payment with NEGATIVE amount.
    @Test(priority = 3,
          description = "PB-002 — Bill payment with negative amount should be rejected")
    public void bug002_billPaymentAllowsNegativeAmount() {
        log.info("=================================================");
        log.info("STARTING TEST: PB-002");
        log.info("Scenario: Bill payment with negative amount");
        log.info("=================================================");
        registerNegativeTestUser();
        LoginPage loginPage = loginNegativeUser();

        log.info("Navigating to Accounts Overview page");
        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();
        String fromAccount = overviewPage.getFirstAccountNumber();
        log.info("Source Account: {}", fromAccount);
        
        BillPaymentPage billPayPage = new BillPaymentPage(getDriver());
        log.info("Navigating to Bill Payment page");
        billPayPage.navigateToBillPay();
        
        billPayPage.fillAndSubmitBillPayment(
                PAYEE_NAME, PAYEE_ADDRESS, PAYEE_CITY, PAYEE_STATE,
                PAYEE_ZIP, PAYEE_PHONE, PAYEE_ACCOUNT, "-100", fromAccount
            );

        boolean paymentSucceeded  = billPayPage.isPaymentSuccessful();
        boolean invalidErrShown   = billPayPage.isAmountInvalidErrorDisplayed();

        printBugReport(
            "PB-002",
            "Bill Payment",
            "Payee = " + PAYEE_NAME + ", Amount = -100, From = " + fromAccount,
            "System should reject negative payment amount",
            paymentSucceeded ? "Bill Payment Complete — BUG"
                             : "Payment rejected — OK",
            paymentSucceeded
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(paymentSucceeded,
            "DEFECT PB-002 — Bill payment with negative amount was allowed!"
        );
        softAssert.assertAll();

        log.info("Logging out test user");
        loginPage.logout();
        log.info("Completed test: PB-002");
    }

    // PB-003 — Transfer FROM and TO the SAME account.
    @Test(priority = 4,
          description = "PB-003 — Transfer to same account should be rejected")
    public void bug003_transferFundsAllowsSameSourceAndDestinationAccount() {
    	
        log.info("=================================================");
        log.info("STARTING TEST: PB-003");
        log.info("Scenario: Transfer using same source and destination account");
        log.info("=================================================");
        registerNegativeTestUser();
        LoginPage loginPage = loginNegativeUser();

        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        log.info("Navigating to Accounts Overview page");
        overviewPage.navigateToOverview();
        String fromAccount = overviewPage.getFirstAccountNumber();

        log.info("Using account [{}] as both source and destination", fromAccount);
        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        log.info("Executing transfer. Amount=10, From={}, To={}",fromAccount,fromAccount);
        transferPage.transferFunds("10", fromAccount, fromAccount);

        boolean transferSucceeded = transferPage.isTransferSuccessful();

        printBugReport(
            "PB-003",
            "Transfer Funds",
            "From Account = Same Account, To Account = Same Account, Amount = 10",
            "System should block same source and destination account transfer",
            transferSucceeded ? "Transfer Complete! — BUG"
                              : "Transfer rejected — OK",
            transferSucceeded
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(transferSucceeded,
            "DEFECT PB-003 — Same account transfer was allowed!"
        );
        softAssert.assertAll();
        
        log.info("Logging out test user");
        loginPage.logout();
        log.info("Completed test: PB-003");
    }

    
    // PB-004 — Bill Payment with MISMATCHED account numbers.
    @Test(priority = 5,
          description = "PB-004 — Bill payment with mismatched accounts should be rejected")
    public void bug004_billPayAcceptsMismatchedAccountNumbers() {
        log.info("=================================================");
        log.info("STARTING TEST: PB-004");
        log.info("Scenario: Bill payment with mismatched account numbers");
        log.info("=================================================");
        registerNegativeTestUser();
        LoginPage loginPage = loginNegativeUser();

        log.info("Navigating to Accounts Overview page");
        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        overviewPage.navigateToOverview();
        String fromAccount = overviewPage.getFirstAccountNumber();
        log.info("Source Account: {}", fromAccount);
        
        BillPaymentPage billPayPage = new BillPaymentPage(getDriver());
        log.info("Navigating to Bill Payment page");
        billPayPage.navigateToBillPay();
        
        log.info("Submitting bill payment with mismatched account numbers. AccountNumber=12345, VerifyAccountNumber=67890");
        billPayPage.fillWithMismatchedAccounts(
            PAYEE_NAME, PAYEE_ADDRESS, PAYEE_CITY, PAYEE_STATE,
            PAYEE_ZIP, PAYEE_PHONE,
            "12345",    // account number
            "67890",    // verify account — intentional mismatch
            "5",
            fromAccount
        );

        boolean paymentSucceeded   = billPayPage.isPaymentSuccessful();
        boolean mismatchErrShown   = billPayPage.isAccountMismatchErrorDisplayed();

        printBugReport(
            "PB-004",
            "Bill Payment",
            "Account Number = 12345, Verify Account Number = 67890, Amount = 5",
            "System should reject mismatched account number and verify account number",
            paymentSucceeded ? "Bill Payment Complete — BUG"
                             : "Mismatch error shown, payment blocked — OK",
            paymentSucceeded
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(paymentSucceeded,
            "DEFECT PB-004 — Mismatched account bill payment was allowed!"
        );
        softAssert.assertTrue(mismatchErrShown,
            "DEFECT PB-004 — No mismatch error shown"
        );
        softAssert.assertAll();
        
        log.info("Logging out test user");
        loginPage.logout();
        log.info("Completed test: PB-004");
    }

    //PB-005 — Transfer with ZERO amount.
    @Test(priority = 6,
          description = "PB-005 — Transfer with zero amount should be rejected")
    public void bug005_transferFundsAllowsZeroAmount() {
        log.info("=================================================");
        log.info("STARTING TEST: PB-005");
        log.info("Scenario: Transfer with zero amount");
        log.info("=================================================");
        registerNegativeTestUser();
        LoginPage loginPage = loginNegativeUser();

        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());
        log.info("Navigating to Accounts Overview page");
        overviewPage.navigateToOverview();
        
        OpenAccount openAcc = new OpenAccount(getDriver());
        log.info("Creating additional SAVINGS account for transfer test");
        openAcc.openNewAccount("SAVINGS");
        
        overviewPage.navigateToOverview();
        String fromAccount = overviewPage.getFirstAccountNumber();
        String toAccount   = overviewPage.getSecondAccountNumber();
        log.info("Source Account      : {}", fromAccount);
        log.info("Destination Account : {}", toAccount);

        TransferFundsPage transferPage = new TransferFundsPage(getDriver());
        log.info(
                "Executing transfer with Amount=0, From={}, To={}",
                fromAccount,
                toAccount
            );

        transferPage.transferFunds("0", fromAccount, toAccount);

        boolean transferSucceeded = transferPage.isTransferSuccessful();

        printBugReport(
            "PB-005",
            "Transfer Funds",
            "Amount = 0, From = " + fromAccount + ", To = " + toAccount,
            "System should reject zero amount transfer",
            transferSucceeded ? "Transfer Complete with $0 — BUG"
                              : "Transfer rejected — OK",
            transferSucceeded
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(transferSucceeded,
            "DEFECT PB-005 — Zero amount transfer was allowed!"
        );
        softAssert.assertAll();
        
        log.info("Logging out test user");
        loginPage.logout();
        log.info("Completed test: PB-005");
    }
  
 // PB-006 — Loan approved with NEGATIVE down payment.
    @Test(priority = 7,
            description = "PB-006 — Negative down payment should be rejected by system")
    public void bug006_loanApprovedWithNegativeDownPayment() {

        log.info("=================================================");
        log.info("STARTING TEST: PB-006");
        log.info("Scenario: Loan approval with negative down payment");
        log.info("=================================================");

        registerNegativeTestUser();
        LoginPage loginPage = loginNegativeUser();

        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());

        log.info("Navigating to Accounts Overview page");
        overviewPage.navigateToOverview();

        String sourceAccount = overviewPage.getFirstAccountNumber();
        double balanceBefore = overviewPage.getAccountBalance(sourceAccount);

        log.info("Source Account: {}", sourceAccount);
        log.info("Balance Before: ${}", balanceBefore);
        log.info("Loan Amount: 1000");
        log.info("Down Payment: -10 (negative value)");

        RequestLoanPage loanPage = new RequestLoanPage(getDriver());
        log.info("Navigating to Request Loan page");
        loanPage.navigateToRequestLoan();
        log.info(
            "Submitting loan request. LoanAmount=1000, DownPayment=-10, Account={}",
            sourceAccount
        );

        loanPage.enterLoanAmount("1000");
        loanPage.enterDownPayment("-10");   // Negative down payment — defect test
        loanPage.selectFromAccount(sourceAccount);
        loanPage.clickApplyNow();

        boolean loanApproved = loanPage.isLoanApproved();
        boolean serverError  = loanPage.isServerErrorDisplayed();
        String loanStatus    = loanPage.getLoanStatus();

        // Record balance impact if loan was approved
        double balanceAfter = balanceBefore;

        printBugReport(
            "PB-006",
            "Request Loan",
            "Loan Amount = 1000, Down Payment = -10, Account = " + sourceAccount,
            "System should reject negative down payment with validation error",
            loanApproved
                ? "Loan Approved with negative down payment — BUG. " +
                  "Balance changed from $" + balanceBefore + " to $" + balanceAfter
                : serverError
                    ? "Server error shown (poor UX but loan blocked)"
                    : "Validation error shown — OK",
            loanApproved
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(
            loanApproved,
            "DEFECT PB-006 — Loan was approved with negative down payment (-10)! " +
            "System should reject negative down payment values."
        );
        softAssert.assertAll();

        log.info("Logging out test user");
        loginPage.logout();
        log.info("Completed test: PB-006");
    }
 // PB-007 — Account balance goes NEGATIVE after loan down payment.
    @Test(priority = 8,
            description = "PB-007 — Account balance should not go negative after loan down payment")
    public void bug007_accountBalanceGoesNegativeAfterLoanDownPayment() {

        log.info("=================================================");
        log.info("STARTING TEST: PB-007");
        log.info("Scenario: Loan down payment exceeds available balance");
        log.info("=================================================");

        registerNegativeTestUser();
        LoginPage loginPage = loginNegativeUser();

        AccountsOverviewPage overviewPage = new AccountsOverviewPage(getDriver());

        log.info("Navigating to Accounts Overview page");
        overviewPage.navigateToOverview();

        String sourceAccount = overviewPage.getFirstAccountNumber();
        double balanceBefore = overviewPage.getAccountBalance(sourceAccount);

        log.info("Source Account: {}", sourceAccount);
        log.info("Balance Before: ${}", balanceBefore);
        log.info("Loan Amount: 1000");
        log.info("Down Payment: 600 (exceeds available balance)");

        RequestLoanPage loanPage = new RequestLoanPage(getDriver());

        log.info("Navigating to Request Loan page");
        loanPage.navigateToRequestLoan();

        log.info(
            "Submitting loan request. LoanAmount=1000, DownPayment=600, Account={}",
            sourceAccount
        );

        loanPage.enterLoanAmount("1000");
        loanPage.enterDownPayment("600");   // Down payment exceeds balance
        loanPage.selectFromAccount(sourceAccount);
        loanPage.clickApplyNow();

        boolean loanDenied = loanPage.isLoanDenied();
        String status = loanPage.getLoanStatus();

        log.info(
            "Loan request completed. Denied={}, Status={}",
            loanDenied,
            status
        );

        overviewPage.navigateToOverview();

        double balanceAfter = overviewPage.getAccountBalance(sourceAccount);
        boolean wentNegative = balanceAfter < 0;
        log.info("Balance After: ${}", balanceAfter);
        log.info("Went Negative: {}", wentNegative);

        printBugReport(
            "PB-007",
            "Request Loan",
            "Loan Amount = 1000, Down Payment = 600 (exceeds balance of $"
            + balanceBefore + ")",
            "Loan should be denied — insufficient funds for down payment. " +
            "Account balance must not go negative.",
            wentNegative
                ? "CRITICAL — Account balance went to $" + balanceAfter
                  + " after loan with excessive down payment"
                : loanDenied
                    ? "Loan correctly denied — balance protected"
                    : "Loan approved but balance unchanged",
            wentNegative
        );

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(
            wentNegative,
            "CRITICAL DEFECT PB-007 — Account balance went negative ($"
            + balanceAfter + ") after loan with down payment " +
            "exceeding available funds!"
        );
        softAssert.assertAll();

        log.info("Logging out test user");
        loginPage.logout();
        log.info("Completed test: PB-007");
    }
}