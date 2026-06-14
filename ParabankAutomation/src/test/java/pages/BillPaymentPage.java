package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BillPaymentPage {
	public static WebDriver driver;
	public static WebDriverWait wait;
	
	private By billPayLink = By.linkText("Bill Pay");
	
    private By payeeName      = By.name("payee.name");
    private By payeeAddress   = By.name("payee.address.street");
    private By payeeCity      = By.name("payee.address.city");
    private By payeeState     = By.name("payee.address.state");
    private By payeeZipCode   = By.name("payee.address.zipCode");
    private By payeePhone     = By.name("payee.phoneNumber");
    private By payeeAccount   = By.name("payee.accountNumber");
    private By verifyAccount  = By.name("verifyAccount");
    private By amount         = By.name("amount");
    private By fromAccountDrop = By.name("fromAccountId");
    
    //submit 
    private By sendPaymentBtn = By.xpath("//input[@value='Send Payment']");
    private By billpayResult  = By.id("billpayResult");
    private By billpayError   = By.id("billpayError");

    // Elements inside the success result div
    private By resultPayeeName = By.id("payeeName");
    private By resultAmount    = By.id("amount");
    private By resultAccountId = By.id("fromAccountId");
    
    private By errorPayeeName     = By.id("validationModel-name");
    private By errorAddress       = By.id("validationModel-address");
    private By errorCity          = By.id("validationModel-city");
    private By errorState         = By.id("validationModel-state");
    private By errorZipCode       = By.id("validationModel-zipCode");
    private By errorPhone         = By.id("validationModel-phoneNumber");
    private By errorAccountEmpty  = By.id("validationModel-account-empty");
    private By errorAccountInvalid= By.id("validationModel-account-invalid");
    private By errorVerifyMismatch= By.id("validationModel-verifyAccount-mismatch");
    private By errorAmountEmpty   = By.id("validationModel-amount-empty");
    private By errorAmountInvalid = By.id("validationModel-amount-invalid");
    
    public BillPaymentPage(WebDriver driver) {
    	this.driver = driver;
    	this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }
    
    public void navigateToBillPay() {
        wait.until(ExpectedConditions.elementToBeClickable(billPayLink)).click();
        // Wait for the form div to appear (JavaScript shows it on load)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("billpayForm")
        ));
    }
    
    public void fillAndSubmitBillPayment(String name, String address,
            String city, String state,
            String zipCode, String phone,
            String accountNum, String payAmount,
            String fromAccount) {
    		driver.findElement(payeeName).sendKeys(name);
    		driver.findElement(payeeAddress).sendKeys(address);
    		driver.findElement(payeeCity).sendKeys(city);
    		driver.findElement(payeeState).sendKeys(state);
    		driver.findElement(payeeZipCode).sendKeys(zipCode);
    		driver.findElement(payeePhone).sendKeys(phone);
    		driver.findElement(payeeAccount).sendKeys(accountNum);
    		driver.findElement(verifyAccount).sendKeys(accountNum);
    		driver.findElement(amount).sendKeys(payAmount);
		
	        WebElement dropdown = wait.until(
	               ExpectedConditions.visibilityOfElementLocated(fromAccountDrop)
	           );
	        
	        new Select(dropdown).selectByValue(fromAccount);
	        driver.findElement(sendPaymentBtn).click();
    }
    
    public void fillWithMismatchedAccounts(String name, String address,
            String city, String state,
            String zipCode, String phone,
            String accountNum, String wrongVerify,
            String payAmount, String fromAccount) {
    	
				driver.findElement(payeeName).sendKeys(name);
				driver.findElement(payeeAddress).sendKeys(address);
				driver.findElement(payeeCity).sendKeys(city);
				driver.findElement(payeeState).sendKeys(state);
				driver.findElement(payeeZipCode).sendKeys(zipCode);
				driver.findElement(payeePhone).sendKeys(phone);
				driver.findElement(payeeAccount).sendKeys(accountNum);
				driver.findElement(verifyAccount).sendKeys(wrongVerify);
				driver.findElement(amount).sendKeys(payAmount);
				
				WebElement dropdown = wait.until(
				ExpectedConditions.visibilityOfElementLocated(fromAccountDrop)
				);
				new Select(dropdown).selectByValue(fromAccount);
				
				wait.until(ExpectedConditions.elementToBeClickable(sendPaymentBtn)).click();
    }
    
    //  RESULT VERIFICATION   
    public boolean isPaymentSuccessful() {
        try {
            WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(billpayResult)
            );
            System.out.println("Payment result: " + result.getText());
            return result.isDisplayed();
        } catch (Exception e) {
            System.out.println("Payment success div NOT shown");
            return false;
        }
    }
    
    public String getResultPayeeName() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(resultPayeeName)
        ).getText().trim();
    }
    
    public String getResultAmount() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(resultAmount)
        ).getText().trim();
    }
    
    public String getResultAccountId() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(resultAccountId)
        ).getText().trim();
    }
    
    //  VALIDATION ERROR CHECKS  
    public boolean isAccountMismatchErrorDisplayed() {
        try {
            return driver.findElement(errorVerifyMismatch).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isAmountEmptyErrorDisplayed() {
        try {
            return driver.findElement(errorAmountEmpty).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isAmountInvalidErrorDisplayed() {
        try {
            return driver.findElement(errorAmountInvalid).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isPayeeNameErrorDisplayed() {
        try {
            return driver.findElement(errorPayeeName).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isServerErrorDisplayed() {
        try {
            return driver.findElement(billpayError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    private void waitAndType(By locator, String text) {
        WebElement element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.clear();
        element.sendKeys(text);
    }
}
