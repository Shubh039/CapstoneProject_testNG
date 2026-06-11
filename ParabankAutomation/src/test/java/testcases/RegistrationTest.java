package testcases;

import base.BaseTest;
import pages.LoginPage;
import pages.RegistrationPage;
import utilities.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AccountsOverviewPage;

public class RegistrationTest extends BaseTest{
	
	private static String REG_SHEET = "Registration";
	private static String LOGIN_SHEET = "LoginData";
	
	@Test(description = "Register a new user with valid data from Excel", priority = 1)
	public void TC01_testValidReg() {
        String firstName = ExcelUtils.getCellData(REG_SHEET, 1, 0);
        String lastName  = ExcelUtils.getCellData(REG_SHEET, 1, 1);
        String address   = ExcelUtils.getCellData(REG_SHEET, 1, 2);
        String city      = ExcelUtils.getCellData(REG_SHEET, 1, 3);
        String state     = ExcelUtils.getCellData(REG_SHEET, 1, 4);
        String zipCode   = ExcelUtils.getCellData(REG_SHEET, 1, 5);
        String phone     = ExcelUtils.getCellData(REG_SHEET, 1, 6);
        String ssn       = ExcelUtils.getCellData(REG_SHEET, 1, 7);
//        String username = ExcelUtils.getCellData(REG_SHEET, 1, 8) 
//                + "_" + System.currentTimeMillis();
        String username = "Shubhs";
        //String password  = ExcelUtils.getCellData(REG_SHEET, 1, 9);
        String password = "demo";

        System.out.println("⏳ Registering user: " + username);
        
        RegistrationPage registerPage = new RegistrationPage(getDriver());
        registerPage.clickRegisterLink();
        registerPage.fillAndSubmitRegistrationForm(
            firstName, lastName, address, city, state,
            zipCode, phone, ssn, username, password
        ); 
        
        Assert.assertTrue(
                registerPage.isRegistrationSuccessful(),
                "Registration failed! Success message not displayed for user: " + username
        );
        System.out.println("[PASS] Registration successful for user: " + username);
        
        ExcelUtils.setCellData(LOGIN_SHEET, 1, 0, username);
        ExcelUtils.setCellData(LOGIN_SHEET, 1, 1, password);

        System.out.println("=> Credentials saved to LoginData Excel sheet → "+ username + " / " + password);
        
      
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║           YOUR ACCOUNT OVERVIEW            ║");
        System.out.println("╚════════════════════════════════════════════╝");
        
        AccountsOverviewPage accOverview = new AccountsOverviewPage(getDriver());
        accOverview.navigateToOverview();
        accOverview.getAccountHolderName();
        String accNo = accOverview.getFirstAccountNumber();
        accOverview.getAccountBalance(accNo);
        
        
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.logout();
        System.out.println("User logged out successfully ✅");
	}
	
}
