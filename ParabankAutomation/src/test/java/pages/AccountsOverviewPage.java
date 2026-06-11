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

    // ─────────────────────────────────────────────
    //  LOCATORS
    // ─────────────────────────────────────────────
    private By overviewLink = By.linkText("Accounts Overview");
    private By accountTable = By.id("accountTable");
    private By accountBalance   = By.id("balance");
    private By availableBalance = By.id("availableBalance");
    private By accountType      = By.id("accountType");
    private By welcomeUserText = By.cssSelector("p.smallText");

    // Transaction table in account activity
    private By transactionTable = By.id("transactionTable");
    private By noTransactions   = By.id("noTransactions");

    // ─────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────

    public AccountsOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─────────────────────────────────────────────
    //  NAVIGATION
    // ─────────────────────────────────────────────
    public void navigateToOverview() {
        wait.until(ExpectedConditions.elementToBeClickable(overviewLink)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountTable));
    }

    // ─────────────────────────────────────────────
    //  BALANCE READING
    // ─────────────────────────────────────────────
    public double getAccountBalance(String accountNumber) {

        // Build XPath to find the balance cell of the specific account row
        String balanceXPath = "//table[@id='accountTable']//a[text()='"
                              + accountNumber + "']/ancestor::td"
                              + "/following-sibling::td[1]";

        WebElement balanceCell = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath(balanceXPath))
        );

        String balanceText = balanceCell.getText().trim();

        /**
         * CONCEPT — Parsing currency string to double:
         * The balance is shown as "$100.00" or "-$2500.00"
         * We need to convert it to a double for arithmetic comparison.
         * Steps:
         *   1. Replace "$" with nothing
         *   2. Replace "," with nothing (e.g. $1,000.00 → 1000.00)
         *   3. Handle negative: "-$100" → "-100"
         *   4. Parse as double
         */
        return parseCurrency(balanceText);
    }

    /**
     * Reads the available amount of a specific account.
     * Available amount can differ from balance if there are holds.
     *
     * @param accountNumber - the account number to look up
     * @return available amount as double
     */
    public double getAvailableAmount(String accountNumber) {

        String availableXPath = "//table[@id='accountTable']//a[text()='"
                                + accountNumber + "']/ancestor::td"
                                + "/following-sibling::td[2]";

        WebElement availableCell = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath(availableXPath))
        );

        String availableText = availableCell.getText().trim();
        System.out.println("Available for account " + accountNumber + ": " + availableText);

        return parseCurrency(availableText);
    }
    
    public void getAccountHolderName() {
    	String text = driver.findElement(welcomeUserText).getText();
    	String username = text.replace("Welcome", "").trim();
    	System.out.println("Account Holder Name: " + username);
    }

    /**
     * Returns the first account number found in the overview table.
     * We use this when we don't have a specific account number —
     * just need ANY valid account to transfer from/to.
     *
     * @return first account number as String
     */
    public String getFirstAccountNumber() {

        // Wait for table to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountTable));

        // Find all account links in the table
        List<WebElement> accountLinks = driver.findElements(By.xpath("//table[@id='accountTable']//tbody//a"));

        if (accountLinks.isEmpty()) {
            throw new RuntimeException("No accounts found in overview table");
        }

        String firstAccount = accountLinks.get(0).getText().trim();
        System.out.println("First account found: " + firstAccount);
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

        System.out.println("Account not found: " + accNo);
        return false;
    }

    /**
     * Returns the second account number — used as the TO account
     * in transfer tests so FROM and TO are different accounts.
     *
     * @return second account number as String
     */
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
        System.out.println("Second account found: " + secondAccount);
        return secondAccount;
    }

    // ─────────────────────────────────────────────
    //  ACCOUNT ACTIVITY
    // ─────────────────────────────────────────────

    /**
     * Clicks on a specific account number to open its activity page.
     *
     * @param accountNumber - the account number link to click
     */
    public void clickAccountNumber(String accountNumber) {

        By accountLink = By.xpath(
            "//table[@id='accountTable']//a[text()='" + accountNumber + "']"
        );

        wait.until(ExpectedConditions.elementToBeClickable(accountLink)).click();

        // Wait for account details to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountBalance));
        System.out.println("Checking activity page for account....... " + accountNumber);
    }

    /**
     * Gets the balance shown on the Account Details section
     * of the activity page (after clicking an account number).
     *
     * @return balance as double
     */
    public double getActivityPageBalance() {
        String balanceText = wait.until(
            ExpectedConditions.visibilityOfElementLocated(accountBalance)
        ).getText().trim();

        System.out.println("Activity page balance: " + balanceText);
        return parseCurrency(balanceText);
    }

    /**
     * Checks if a debit transaction exists in the transaction table
     * for a specific amount — confirms money left the FROM account.
     *
     * @param amount - the transfer amount to look for (e.g. "100.00")
     * @return true if debit transaction found
     */
    public boolean isDebitTransactionPresent(String amount) {

        try {
            // Wait for transaction table to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));

            /**
             * XPath logic:
             * Find a <td> in the Debit column that contains the amount.
             * Table columns: Date | Transaction | Debit(-) | Credit(+)
             * Debit is the 3rd <td> in each row (index 3).
             * We look for a row where the 3rd td contains our amount.
             */
            String debitXPath = "//table[@id='transactionTable']//tbody//tr"
                              + "/td[3][contains(text(),'" + amount + "')]";

            WebElement debitCell = driver.findElement(By.xpath(debitXPath));
            System.out.println(" -► Debit transaction found: " + debitCell.getText());
            return true;

        } catch (Exception e) {
            System.out.println(" -► Debit transaction NOT found for amount: " + amount);
            return false;
        }
    }

    /**
     * Checks if a credit transaction exists for a specific amount —
     * confirms money arrived in the TO account.
     *
     * @param amount - the transfer amount to look for (e.g. "100.00")
     * @return true if credit transaction found
     */
    public boolean isCreditTransactionPresent(String amount) {

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));

            // Credit is the 4th <td> in each row
            String creditXPath = "//table[@id='transactionTable']//tbody//tr"
                               + "/td[4][contains(text(),'" + amount + "')]";

            WebElement creditCell = driver.findElement(By.xpath(creditXPath));
            System.out.println(" -► Credit transaction found: " + creditCell.getText());
            return true;

        } catch (Exception e) {
            System.out.println(" -► Credit transaction NOT found for amount: " + amount);
            return false;
        }
    }
    
    public boolean isBillPaymentTransactionPresent(String payeeName) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));

            /**
             * XPath explanation:
             * Find a <td> in column 2 (transaction description)
             * that contains "Bill Payment to" AND the payee name.
             * The <a> tag inside that td holds the description text.
             */
            String transactionXPath = "//table[@id='transactionTable']//tbody//tr"
                + "/td[2]/a[contains(text(),'Bill Payment to') "
                + "and contains(text(),'" + payeeName + "')]";

            WebElement transaction = driver.findElement(By.xpath(transactionXPath));
            System.out.println("Transaction found: " + transaction.getText());
            return true;

        } catch (Exception e) {
            System.out.println("Bill Payment transaction NOT found for payee: " + payeeName);
            return false;
        }
    }

    // ─────────────────────────────────────────────
    //  PRIVATE HELPER
    // ─────────────────────────────────────────────

    private double parseCurrency(String currencyText) {
        if (currencyText == null || currencyText.isEmpty()) {
            return 0.0;
        }
        // Remove $, commas, spaces — keep digits, dot, minus
        String cleaned = currencyText.replaceAll("[^0-9.\\-]", "");
        return Double.parseDouble(cleaned);
    }
}