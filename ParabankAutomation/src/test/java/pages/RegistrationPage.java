package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class RegistrationPage {
    private WebDriver driver;
    private WebDriverWait wait;

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
    private By registerBtn  = By.xpath("//input[@value='Register']");     // Submit button
    private By successMessage = By.xpath(
    		"//p[contains(text(),'Your account was created successfully')]");     // SuccessMessage

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickRegisterLink() {
        wait.until(ExpectedConditions.elementToBeClickable(registerLink)).click();
    }

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

        // Clicking Register button
        wait.until(ExpectedConditions.elementToBeClickable(registerBtn)).click();
    }

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
    }

    //  PRIVATE HELPER METHOD
    private void waitAndType(By locator, String text) {
        WebElement element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.clear();
        element.sendKeys(text);
    }
}