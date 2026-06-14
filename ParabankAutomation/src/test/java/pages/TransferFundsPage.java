package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TransferFundsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    //  LOCATORS
    private By transferLink     = By.linkText("Transfer Funds");
    private By amountField      = By.id("amount");
    private By fromAccountDrop  = By.id("fromAccountId");
    private By toAccountDrop    = By.id("toAccountId");
    private By transferButton   = By.xpath("//input[@value='Transfer']");
    private By transferResult   = By.id("showResult");
    private By transferredAmount = By.id("amount");

    // Error message for invalid amount
    private By amountError = By.id("amount.errors");

    //  CONSTRUCTOR
    public TransferFundsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    //  PAGE ACTIONS
    public void navigateToTransferFunds() {
        wait.until(ExpectedConditions.elementToBeClickable(transferLink)).click();
        // Waiting for the form to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountField));
    }

    public void selectFromAccount(String accountNumber) {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(fromAccountDrop)
        );
        new Select(dropdown).selectByValue(accountNumber);
    }
    public void selectToAccount(String accountNumber) {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(toAccountDrop)
        );
        new Select(dropdown).selectByValue(accountNumber);
    }
    
    public void enterAmount(String amount) {
        WebElement field = wait.until(
            ExpectedConditions.visibilityOfElementLocated(amountField)
        );
        field.clear();
        field.sendKeys(amount);
    }

    public void clickTransfer() {
        wait.until(ExpectedConditions.elementToBeClickable(transferButton)).click();
    }

    public void transferFunds(String amount, String fromAccount, String toAccount) {
        navigateToTransferFunds();
        enterAmount(amount);
        selectFromAccount(fromAccount);
        selectToAccount(toAccount);
        clickTransfer();
    }

    public boolean isTransferSuccessful() {
        try {
            WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(transferResult)
            );
            return result.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAmountErrorDisplayed() {
        try {
            WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(amountError)
            );
            return error.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}