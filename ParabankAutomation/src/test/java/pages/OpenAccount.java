package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OpenAccount {
	private static WebDriver driver;
	private static WebDriverWait wait;
	
	private By openAcc = By.linkText("Open New Account");
	private By accType = By.id("type");
	private By fromAccountDropdown = By.id("fromAccountId");
	private By openAccountButton = By.xpath("//input[@value='Open New Account']");
	private By successResult = By.id("openAccountResult");
	private By newAccountId = By.id("newAccountId");
	private By errorResult = By.id("openAccountError");
	
	public OpenAccount(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}
	
    public void navigateToOpenAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(openAcc))
            .click();
    }
    
    public void selectAccountType(String accountType) {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(accType)
        );
        new Select(dropdown).selectByVisibleText(accountType);
    }
    
    public void selectFromAccount() {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(fromAccountDropdown)
        );
        new Select(dropdown).selectByIndex(0);
    }
    
    public void clickOpenNewAccount() {
        driver.findElement(openAccountButton)
            .click();
    }
    
    public boolean isAccountOpenedSuccessfully() {
        try {
            WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(successResult)
            );
            return result.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getNewAccountNumber() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(newAccountId)
        ).getText().trim();
    }
    
    public void openNewAccount(String accountType) {
        navigateToOpenAccount();
        selectAccountType(accountType);
        selectFromAccount();
        clickOpenNewAccount();
    }

}
