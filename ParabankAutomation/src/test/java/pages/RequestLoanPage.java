package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RequestLoanPage {

    private WebDriver driver;
    private WebDriverWait wait;

    //  LOCATORS
    // Navigation link
    private By requestLoanLink = By.linkText("Request Loan");

    // Form fields — all have id attributes, so By.id() is most reliable
    private By loanAmountField   = By.id("amount");
    private By downPaymentField  = By.id("downPayment");
    private By fromAccountDrop   = By.id("fromAccountId");

    // Submit button — type="button" triggers JavaScript AJAX
    private By applyNowButton    = By.xpath("//input[@value='Apply Now']");
    private By loanResultDiv     = By.id("requestLoanResult");
    private By loanStatus        = By.id("loanStatus");
    private By loanProviderName  = By.id("loanProviderName");
    private By newAccountId      = By.id("newAccountId");
    private By loanApprovedDiv   = By.id("loanRequestApproved");
    private By loanDeniedDiv     = By.id("loanRequestDenied");
    private By deniedMessage     = By.xpath("//div[@id='loanRequestDenied']/p");
    private By loanErrorDiv      = By.id("requestLoanError");

    //  CONSTRUCTOR
    public RequestLoanPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    //  NAVIGATION
    public void navigateToRequestLoan() {
        wait.until(ExpectedConditions.elementToBeClickable(requestLoanLink)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountField));
    }

    //  FORM ACTIONS
    public void enterLoanAmount(String amount) {
        WebElement field = wait.until(
            ExpectedConditions.visibilityOfElementLocated(loanAmountField)
        );
        field.clear();
        field.sendKeys(amount);
    }

    public void enterDownPayment(String downPayment) {
        WebElement field = wait.until(
            ExpectedConditions.visibilityOfElementLocated(downPaymentField)
        );
        field.clear();
        field.sendKeys(downPayment);
    }

    public void selectFromAccountByIndex(int index) {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(fromAccountDrop)
        );
        Select select = new Select(dropdown);
        select.selectByIndex(index);
    }

    public void selectFromAccount(String accountNumber) {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(fromAccountDrop)
        );
        new Select(dropdown).selectByValue(accountNumber);
    }

    public void clickApplyNow() {
        wait.until(ExpectedConditions.elementToBeClickable(applyNowButton))
            .click();
    }

    public void applyForLoan(String loanAmount, String downPayment,
                              int accountIndex) {
        navigateToRequestLoan();
        enterLoanAmount(loanAmount);
        enterDownPayment(downPayment);
        selectFromAccountByIndex(accountIndex);
        clickApplyNow();
    }

    //  RESULT VERIFICATION
    public boolean isLoanResultDisplayed() {
        try {
            WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(loanResultDiv)
            );
            return result.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoanApproved() {
        try {
            String status = wait.until(
                ExpectedConditions.visibilityOfElementLocated(loanStatus)
            ).getText().trim();
            return status.equalsIgnoreCase("Approved");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoanDenied() {
        try {
            String status = wait.until(
                ExpectedConditions.visibilityOfElementLocated(loanStatus)
            ).getText().trim();
            return status.equalsIgnoreCase("Denied");
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoanStatus() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOfElementLocated(loanStatus)
            ).getText().trim();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public String getNewLoanAccountNumber() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOfElementLocated(newAccountId)
            ).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDenialReason() {
        try {
            return driver.findElement(deniedMessage).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isServerErrorDisplayed() {
        try {
            return driver.findElement(loanErrorDiv).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoanProviderName() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOfElementLocated(loanProviderName)
            ).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}