package testcases;
import base.BaseTest;
import pages.AccountsOverviewPage;
import pages.LoginPage;
import pages.OpenAccount;
import utilities.ExcelUtils;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.Test;

public class OpenAccountTest extends BaseTest{
	private static String LOGIN_SHEET = "LoginData";
	private static String ACCOUNT_SHEET = "AccountData";
	
	@Test(priority= 1, description = "TC01 - Open a new CHECKING account after login")
	public void TC01_testOpenAccount() {
		String username = ExcelUtils.getCellData(LOGIN_SHEET, 1,0);
		String password = ExcelUtils.getCellData(LOGIN_SHEET, 1,1);
		
		LoginPage loginPage = new LoginPage(getDriver());
		loginPage.login(username, password);
		
        System.out.println("Login successful — proceeding with Opening Account test");
		
        Assert.assertTrue(
                loginPage.isLoginSuccessful(),
                "Login failed before Open Account test"
        );
        
        AccountsOverviewPage accOverview = new AccountsOverviewPage(getDriver());
        accOverview.navigateToOverview();
        
        String sourceAccount = accOverview.getFirstAccountNumber();
        double sourceBalance = accOverview.getAccountBalance(sourceAccount);
        
        System.out.println("\n========== ACCOUNT DETAILS ==========");
        System.out.println("Source Account   : " + sourceAccount);
        System.out.println("Current Balance  : $" + sourceBalance);
        System.out.println("=====================================");
        
        OpenAccount openAcc = new OpenAccount(getDriver());
        openAcc.openNewAccount("CHECKING");
        
        Assert.assertTrue(openAcc.isAccountOpenedSuccessfully(),"CHECKING account was not opened successfully");
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║       NEW CHECKING ACCOUNT CREATED         ║");
        System.out.println("╚════════════════════════════════════════════╝");
        String newAccountNumber = openAcc.getNewAccountNumber();
        System.out.println("[NEW] CHECKING account number: " + newAccountNumber);
        
        // navigate to overview
        accOverview.navigateToOverview();
        Assert.assertTrue(accOverview.findAccountNumber(newAccountNumber),"DEFECT — Newly opened account " 
        + newAccountNumber+ " not visible in Accounts Overview!");
        
        double newAccountBalance = accOverview.getAccountBalance(newAccountNumber);
        System.out.println("[NEW] CHECKING account balance: $" + newAccountBalance);

        Assert.assertEquals(
            newAccountBalance, 100.0, 0.01,
            "DEFECT — New account balance should be $100.00 but was $" + newAccountBalance
        );

        // ── Step 5: Verify source account reduced by $100 ────────────
        double balanceAfter = accOverview.getAccountBalance(sourceAccount);
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
            balanceAfter,
            sourceBalance - 100.0,
            0.01,
            "DEFECT — Source account balance not reduced by $100. " +
            "Expected: $" + (sourceBalance - 100.0) +
            " Actual: $" + balanceAfter
        );

        softAssert.assertAll();
        
        System.out.println("\n[UPDATED] source account balance: $"+balanceAfter);        
        System.out.println("✅ All Open Account verifications passed!");
        
        ExcelUtils.setCellData(ACCOUNT_SHEET, 1, 0, newAccountNumber);
        ExcelUtils.setCellData(ACCOUNT_SHEET, 1, 1, "CHECKING");
        System.out.println("=> New Account number and Account type saved to Excel: " + newAccountNumber);
        
        System.out.println("User logged out successfully ✅");
        loginPage.logout();
	}

	@Test(priority= 2, description = "TC02 - Open a new SAVINGS account after login")
	public void TC02_testOpenAccount() {
		String username = ExcelUtils.getCellData(LOGIN_SHEET, 1,0);
		String password = ExcelUtils.getCellData(LOGIN_SHEET, 1,1);
		
		LoginPage loginPage = new LoginPage(getDriver());
		loginPage.login(username, password);
        System.out.println("Login successful — proceeding with Opening Account test");
		
        Assert.assertTrue(
                loginPage.isLoginSuccessful(),
                "Login failed before Open Account test"
        );
        
        AccountsOverviewPage accOverview = new AccountsOverviewPage(getDriver());
        accOverview.navigateToOverview();
        
        String sourceAccount = accOverview.getFirstAccountNumber();
        double sourceBalance = accOverview.getAccountBalance(sourceAccount);
        
        System.out.println("\n========== ACCOUNT DETAILS ==========");
        System.out.println("Source Account   : " + sourceAccount);
        System.out.println("Current Balance  : $" + sourceBalance);
        System.out.println("=====================================");
        
        OpenAccount openAcc = new OpenAccount(getDriver());
        openAcc.openNewAccount("SAVINGS");
        
        Assert.assertTrue(openAcc.isAccountOpenedSuccessfully(),"SAVINGS account was not opened successfully");
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║        NEW SAVINGS ACCOUNT CREATED         ║");
        System.out.println("╚════════════════════════════════════════════╝");
        String newAccountNumber = openAcc.getNewAccountNumber();
        System.out.println("[NEW] SAVINGS account number: " + newAccountNumber);
        
        // navigate to overview
        accOverview.navigateToOverview();
        Assert.assertTrue(accOverview.findAccountNumber(newAccountNumber),"DEFECT — Newly opened account " 
        + newAccountNumber+ " not visible in Accounts Overview!");
        
        double newAccountBalance = accOverview.getAccountBalance(newAccountNumber);
        System.out.println("[NEW] SAVINGS account balance: $" + newAccountBalance);

        Assert.assertEquals(
            newAccountBalance, 100.0, 0.01,
            "DEFECT — New account balance should be $100.00 but was $" + newAccountBalance
        );

        // ── Step 5: Verify source account reduced by $100 ────────────
        double balanceAfter = accOverview.getAccountBalance(sourceAccount);
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
            balanceAfter,
            sourceBalance - 100.0,
            0.01,
            "DEFECT — Source account balance not reduced by $100. " +
            "Expected: $" + (sourceBalance - 100.0) +
            " Actual: $" + balanceAfter
        );

        softAssert.assertAll();
        
        System.out.println("\n[UPDATED] Source Account Balance: $"+balanceAfter);        
        System.out.println("✅ All Open Account verifications passed!");
        
        ExcelUtils.setCellData(ACCOUNT_SHEET, 1, 0, newAccountNumber);
        ExcelUtils.setCellData(ACCOUNT_SHEET, 1, 1, "SAVINGS");
        System.out.println("=> New Account number and Account type saved to Excel: " + newAccountNumber);
        
        System.out.println("User logged out successfully ✅");
        loginPage.logout();
	}
}
