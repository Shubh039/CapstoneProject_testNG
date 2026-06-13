package base;

import utilities.ConfigReader;
import utilities.ScreenShotUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.apache.logging.log4j.Logger;
import utilities.LoggerUtil;

public class BaseTest {
    // For parallel execution
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    // Logger
    private static final Logger log = LoggerUtil.getLogger(BaseTest.class);
    // Returns the driver for current thread
    public WebDriver getDriver() {
        return driver.get();
    }

    @BeforeMethod
    public void setUp() {
        // Reading the browser name from config.properties
        String browser = ConfigReader.get("browser").toLowerCase();
        log.info("====================================================");
        log.info("Setting up browser: {}", browser);
        WebDriver webDriver;

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-infobars");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--remote-allow-origins=*");

                String envHeadless  = System.getenv("headless");
                String propHeadless = System.getProperty("headless", "false");
                String osName       = System.getProperty("os.name", "").toLowerCase();
                boolean isLinux     = osName.contains("linux");

                boolean runHeadless = "true".equals(envHeadless)
                                      || "true".equals(propHeadless)
                                      || isLinux;

                if (runHeadless) {
                    chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    log.info("Running Chrome in HEADLESS mode (Linux/Docker/CI)");
                } else {
                    chromeOptions.addArguments("--start-maximized");
                    log.info("Running Chrome in NORMAL mode (Windows/local)");
                }

                webDriver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--start-maximized");
                webDriver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                webDriver = new EdgeDriver(edgeOptions);
                break;

            default:
                // If unknown browser
                throw new RuntimeException(
                    "Browser '" + browser + "' is not supported. " +
                    "Use: chrome, firefox, or edge in config.properties"
                );
        }
        // Store the driver in ThreadLocal for this thread
        driver.set(webDriver);
        // Read URL from config and open it in the browser
        getDriver().get(ConfigReader.get("url"));
        log.info("Browser launched. URL: {}", ConfigReader.get("url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {    	
        // ITestResult.FAILURE shows that the test method threw an assertion error or exception
        if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("TEST PASSED ✅ : {}", result.getName());
        } else if (result.getStatus() == ITestResult.FAILURE) {
            log.error("TEST FAILED ❌ : {}", result.getName());
            log.error("Failure reason: {}", result.getThrowable().getMessage());
        	ScreenShotUtils.takeScreenshot(getDriver(), result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("TEST SKIPPED ⚠️ : {}", result.getName());
        }
        // getDriver() != null prevents NullPointerException
        // Quit the driver
        if (getDriver() != null) {
            getDriver().quit();
            log.info("Browser closed");
        }
        // remove the current driver
        driver.remove();
        log.info("====================================================\n");
    }
}