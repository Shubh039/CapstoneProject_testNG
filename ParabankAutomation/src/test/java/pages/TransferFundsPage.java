package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * TransferFundsPage handles the Transfer Funds page on Parabank.
 *
 * The transfer form has:
 *   - Amount input field
 *   - From account dropdown
 *   - To account dropdown
 *   - Transfer submit button
 *
 * After successful transfer, a confirmation div appears.
 * We verify this AND then cross-check balances on the overview page.
 */
public class TransferFundsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ─────────────────────────────────────────────
    //  LOCATORS
    // ─────────────────────────────────────────────

    private By transferLink     = By.linkText("Transfer Funds");
    private By amountField      = By.id("amount");
    private By fromAccountDrop  = By.id("fromAccountId");
    private By toAccountDrop    = By.id("toAccountId");
    private By transferButton   = By.xpath("//input[@value='Transfer']");

    /**
     * Success div shown after transfer completes.
     * Parabank shows: "Transfer Complete! $100.00 has been transferred..."
     */
    private By transferResult   = By.id("showResult");

    /**
     * The amount shown in the success message.
     * We read this to confirm the correct amount was transferred.
     */
    private By transferredAmount = By.id("amount");

    // Error message for invalid amount
    private By amountError = By.id("amount.errors");

    // ─────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────

    public TransferFundsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─────────────────────────────────────────────
    //  PAGE ACTIONS
    // ─────────────────────────────────────────────

    /**
     * Navigates to Transfer Funds page from the nav menu.
     */
    public void navigateToTransferFunds() {
        wait.until(ExpectedConditions.elementToBeClickable(transferLink)).click();
        // Wait for the form to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountField));
    }

    /**
     * Selects the FROM account by account number value.
     *
     * CONCEPT — selectByValue():
     * The dropdown option has value="12345" matching the account number.
     * selectByValue() is more reliable than selectByVisibleText()
     * when the option value directly matches what we want.
     *
     * @param accountNumber - account number to transfer FROM
     */
    public void selectFromAccount(String accountNumber) {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(fromAccountDrop)
        );
        new Select(dropdown).selectByValue(accountNumber);
    }

    /**
     * Selects the TO account by account number value.
     *
     * @param accountNumber - account number to transfer TO
     */
    public void selectToAccount(String accountNumber) {
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(toAccountDrop)
        );
        new Select(dropdown).selectByValue(accountNumber);
    }

    /**
     * Enters the transfer amount.
     *
     * @param amount - amount as string e.g. "100"
     */
    public void enterAmount(String amount) {
        WebElement field = wait.until(
            ExpectedConditions.visibilityOfElementLocated(amountField)
        );
        field.clear();
        field.sendKeys(amount);
        System.out.println("Transfer Amount: $" + amount);
    }

    /**
     * Clicks the Transfer button to submit the form.
     */
    public void clickTransfer() {
        wait.until(ExpectedConditions.elementToBeClickable(transferButton)).click();
    }

    /**
     * Complete transfer in one method.
     *
     * @param amount        - amount to transfer
     * @param fromAccount   - source account number
     * @param toAccount     - destination account number
     */
    public void transferFunds(String amount, String fromAccount, String toAccount) {
        navigateToTransferFunds();
        enterAmount(amount);
        selectFromAccount(fromAccount);
        selectToAccount(toAccount);
        clickTransfer();
    }

    /**
     * Checks if transfer was successful by waiting for
     * the result div to become visible.
     *
     * @return true if success message shown
     */
    public boolean isTransferSuccessful() {
        try {
            WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(transferResult)
            );
            System.out.println("Transfer result : " + result.getText());
            return result.isDisplayed();
        } catch (Exception e) {
            System.out.println("Transfer result NOT shown");
            return false;
        }
    }

    /**
     * Checks if the amount error is shown — used in negative tests.
     * Parabank shows this when amount is empty or invalid.
     *
     * @return true if error message is visible
     */
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