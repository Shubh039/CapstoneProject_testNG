package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class RegistrationPage {

    // WebDriver instance — passed in from the test class
    private WebDriver driver;

    // WebDriverWait — used to wait for elements before interacting
    private WebDriverWait wait;

    // Link on home page to go to registration
    private By registerLink = By.linkText("Register");

    // Registration form fields — using id locators from the HTML
    private By firstName    = By.id("customer.firstName");
    private By lastName     = By.id("customer.lastName");
    private By address      = By.id("customer.address.street");
    private By city         = By.id("customer.address.city");
    private By state        = By.id("customer.address.state");
    private By zipCode      = By.id("customer.address.zipCode");
    private By phone        = By.id("customer.phoneNumber");
    private By ssn          = By.id("customer.ssn");
    private By username     = By.id("customer.username");
    private By password     = By.id("customer.password");
    private By confirmPass  = By.id("repeatedPassword");
    private By logOut = By.linkText("Log Out");

    // Submit button
    private By registerBtn  = By.xpath("//input[@value='Register']");
    
    // SuccessMessage
    private By successMessage = By.xpath("//p[contains(text(),'Your account was created successfully')]");

    // ─────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────

    /**
     * Constructor receives the WebDriver from the test class.
     *
     * CONCEPT — Dependency Injection:
     * We don't create a new WebDriver here. The test class
     * already has one (from BaseTest). We inject it here
     * so this page works with the same browser session.
     *
     * WebDriverWait(driver, Duration.ofSeconds(10)) means:
     * "Wait up to 10 seconds for a condition before throwing TimeoutException"
     */
    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ─────────────────────────────────────────────
    //  PAGE ACTIONS — Methods that represent user actions
    // ─────────────────────────────────────────────

    /**
     * Clicks the Register link on the home page.
     * We wait for it to be clickable first — it may not
     * be ready immediately if the page is still loading.
     */
    public void clickRegisterLink() {
        wait.until(ExpectedConditions.elementToBeClickable(registerLink)).click();
    }

    /**
     * Fills in the entire registration form and submits it.
     *
     * CONCEPT — Why one method for all fields:
     * Registration is ONE user action — fill the form and submit.
     * We model it as one method so the test reads naturally:
     *   registerPage.fillAndSubmitRegistrationForm(...)
     *
     * Each field uses waitAndType() helper (defined below)
     * which waits for visibility before typing — safer than
     * typing immediately which can fail if page is slow.
     *
     * @param fn   - First Name
     * @param ln   - Last Name
     * @param addr - Street Address
     * @param ct   - City
     * @param st   - State
     * @param zip  - Zip Code
     * @param ph   - Phone number
     * @param ssn  - Social Security Number
     * @param user - Username
     * @param pass - Password
     */
    public void fillAndSubmitRegistrationForm(String fn, String ln, String addr,
                                               String ct, String st, String zip,
                                               String ph, String ssnVal,
                                               String user, String pass) {
        waitAndType(firstName,   fn);
        waitAndType(lastName,    ln);
        waitAndType(address,     addr);
        waitAndType(city,        ct);
        waitAndType(state,       st);
        waitAndType(zipCode,     zip);
        waitAndType(phone,       ph);
        waitAndType(ssn,         ssnVal);
        waitAndType(username,    user);
        waitAndType(password,    pass);
        waitAndType(confirmPass, pass); // Confirm password = same as password

        // Click Register button
        wait.until(ExpectedConditions.elementToBeClickable(registerBtn)).click();
    }

    /**
     * Checks if registration was successful by looking for
     * the success message on the page.
     *
     * @return true if success message is displayed, false otherwise
     */
    public boolean isRegistrationSuccessful() {
        try {
            WebElement msg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(successMessage)
            );
            return msg.isDisplayed();
        } catch (Exception e) {
            // If message never appeared within wait time, return false
            return false;
        }
    }
    
    public void logout() {
    	driver.findElement(logOut).click();
    	System.out.println("Successfully logged out---------------------------");
    }

    // ─────────────────────────────────────────────
    //  PRIVATE HELPER METHOD
    // ─────────────────────────────────────────────

    /**
     * Waits for an element to be visible, then clears it and types.
     *
     * CONCEPT — Why clear() before sendKeys():
     * If the field has any default value, sendKeys() would
     * append to it. clear() ensures we start fresh every time.
     *
     * CONCEPT — Explicit Wait vs Implicit Wait:
     * Implicit wait applies globally and can slow tests down.
     * Explicit wait (WebDriverWait) waits ONLY for a specific
     * condition on a specific element — more precise and faster.
     *
     * @param locator - the By locator of the field
     * @param text    - text to type into the field
     */
    private void waitAndType(By locator, String text) {
        WebElement element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.clear();
        element.sendKeys(text);
    }
}