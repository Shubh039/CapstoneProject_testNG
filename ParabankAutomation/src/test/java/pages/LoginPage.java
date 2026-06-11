package pages;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage{
	private WebDriver driver;
	private WebDriverWait wait;
	
	private By usernameField = By.xpath("//form[@name='login']//input[@name='username']");
	private By passwordField = By.xpath("//form[@name='login']//input[@name='password']");
	private By loginBtn = By.xpath("//input[@value = 'Log In']");
    private By errorMissing = By.xpath("//p[contains(text(),'Please enter a username and password.')]");
    private By errorMessage = By.xpath("//p[contains(text(),'The username and password could not be verified')]");
    private By logOut = By.linkText("Log Out");
    
    public LoginPage(WebDriver driver) {
    	this.driver = driver;
    	this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public void login(String username, String password) {
    	wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField)).sendKeys(username);
    	driver.findElement(passwordField).sendKeys(password);
    	driver.findElement(loginBtn).click();
    }
    
    public boolean isLoginSuccessful() {
        try {
            return wait.until(
                ExpectedConditions.urlContains("overview.htm")
            );
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isLoginErrorDisplayed() {
    	try {
    		WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
    		return error.isDisplayed();
    	} catch(Exception e) {
    		return false;
    	}
    }
    public boolean isLoginMissingErrorDisplayed() {
    	try {
    		WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMissing));
    		return error.isDisplayed();
    	} catch(Exception e) {
    		return false;
    	}
    }
    
    public void logout() {
    	wait.until(ExpectedConditions.elementToBeClickable(logOut)).click();
    }
}
