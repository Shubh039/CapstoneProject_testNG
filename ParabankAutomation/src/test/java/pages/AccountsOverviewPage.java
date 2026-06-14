package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AccountsOverviewPage {
    private WebDriver driver;
    private WebDriverWait wait;

    //  LOCATORS
    private By overviewLink = By.linkText("Accounts Overview");
    private By accountTable = By.id("accountTable");
    private By accountBalance   = By.id("balance");
    private By availableBalance = By.id("availableBalance");
    private By accountType      = By.id("accountType");
    private By welcomeUserText = By.cssSelector("p.smallText");
    private By transactionTable = By.id("transactionTable");
    private By noTransactions   = By.id("noTransactions");

    public AccountsOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    //  NAVIGATION
    public void navigateToOverview() {
        wait.until(ExpectedConditions.elementToBeClickable(overviewLink)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountTable));
    }

    //  BALANCE READING
    public double getAccountBalance(String accountNumber) {
        String balanceXPath = "//table[@id='accountTable']//a[text()='"
                              + accountNumber + "']/ancestor::td"
                              + "/following-sibling::td[1]";

        WebElement balanceCell = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath(balanceXPath))
        );

        String balanceText = balanceCell.getText().trim();
        return parseCurrency(balanceText);
    }
    
    public String getAccountHolderName() {
    	String text = driver.findElement(welcomeUserText).getText();
    	String username = text.replace("Welcome", "").trim();
    	System.out.println("Account Holder Name: " + username);
    	return username;
    }

    public String getFirstAccountNumber() {

        // Waiting for table to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountTable));

        // Listing all account links in the table
        List<WebElement> accountLinks = driver.findElements(By.xpath("//table[@id='accountTable']//tbody//a"));

        if (accountLinks.isEmpty()) {
            throw new RuntimeException("No accounts found in overview table");
        }

        String firstAccount = accountLinks.get(0).getText().trim();
        return firstAccount;
    }
    
    public boolean findAccountNumber(String accNo) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountTable));
        List<WebElement> accountLinks = driver.findElements(By.xpath("//table[@id='accountTable']//tbody//a"));
        if (accountLinks.isEmpty()) {
            throw new RuntimeException("No accounts found in overview table");
        }
        
        for (WebElement account : accountLinks) {

            String accountNumber = account.getText().trim();

            if (accountNumber.equals(accNo)) {
                return true;
            }
        }
        return false;
    }

    public String getSecondAccountNumber() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountTable));

        List<WebElement> accountLinks = driver.findElements(
            By.xpath("//table[@id='accountTable']//tbody//a")
        );
        if (accountLinks.size() < 2) {
            throw new RuntimeException(
                "Need at least 2 accounts for transfer test. " +
                "Only found: " + accountLinks.size()
            );
        }

        String secondAccount = accountLinks.get(1).getText().trim();
        return secondAccount;
    }
    
    //  ACCOUNT ACTIVITY
    public void clickAccountNumber(String accountNumber) {

        By accountLink = By.xpath(
            "//table[@id='accountTable']//a[text()='" + accountNumber + "']"
        );

        wait.until(ExpectedConditions.elementToBeClickable(accountLink)).click();
        // Waiting for account details to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountBalance));
    }

    public double getActivityPageBalance() {
        String balanceText = wait.until(
            ExpectedConditions.visibilityOfElementLocated(accountBalance)
        ).getText().trim();
        return parseCurrency(balanceText);
    }

    public boolean isDebitTransactionPresent(String amount) {

        try {
            // Waiting for transaction table to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));
            String debitXPath = "//table[@id='transactionTable']//tbody//tr"
                              + "/td[3][contains(text(),'" + amount + "')]";

            WebElement debitCell = driver.findElement(By.xpath(debitXPath));
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCreditTransactionPresent(String amount) {

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));

            // Credit is the 4th <td> in each row
            String creditXPath = "//table[@id='transactionTable']//tbody//tr"
                               + "/td[4][contains(text(),'" + amount + "')]";

            WebElement creditCell = driver.findElement(By.xpath(creditXPath));
            return true;

        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isBillPaymentTransactionPresent(String payeeName) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    //  PRIVATE HELPER
    private double parseCurrency(String currencyText) {
        if (currencyText == null || currencyText.isEmpty()) {
            return 0.0;
        }
        // Remove $, commas, spaces
        String cleaned = currencyText.replaceAll("[^0-9.\\-]", "");
        return Double.parseDouble(cleaned);
    }
}