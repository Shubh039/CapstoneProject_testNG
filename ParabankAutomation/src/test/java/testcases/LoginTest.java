package testcases;

import org.testng.Assert;
import org.testng.annotations.Test;
import base.BaseTest;
import pages.LoginPage;
import utilities.ExcelUtils;

public class LoginTest extends BaseTest{
	private static String LOGIN_SHEET = "LoginData";
	
    @Test(priority = 1, description = "TC01 - Login with valid credentials from Excel")
    public void TC01_validLogin() {
        String username = ExcelUtils.getCellData(LOGIN_SHEET, 1, 0);
        String password = ExcelUtils.getCellData(LOGIN_SHEET, 1, 1);
        
    	LoginPage loginPage = new LoginPage(getDriver());
    	loginPage.login(username, password);
    	boolean succ = loginPage.isLoginSuccessful();
    	Assert.assertTrue(succ, "Valid Login Failed for user");
    	
    	System.out.println("[PASS] Login is successful for: " + username);
    	loginPage.logout();
        System.out.println("User logged out successfully ✅");
    }

 }

