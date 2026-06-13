package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.Logger;

public class ExtentReportsManager {
    private static final Logger log = LoggerUtil.getLogger(ExtentReportsManager.class);
    // Single ExtentReports instance for the entire test suite
    private static ExtentReports extent;
    // ThreadLocal ensures each test thread gets its own ExtentTest
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static void initReports() {
        // Defining path
        String reportPath = "reports/AutomationReport.html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

        sparkReporter.config().setDocumentTitle("Parabank Automation Report");
        sparkReporter.config().setReportName("Selenium TestNG Automation Results");
        sparkReporter.config().setTheme(Theme.DARK);   // DARK or STANDARD
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        sparkReporter.config().setEncoding("UTF-8");
        
        sparkReporter.config().setOfflineMode(true);
        // Creating the main ExtentReports object
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Adding the system info shown in the report's environment section
        extent.setSystemInfo("Application", "Parabank Demo Banking App");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Framework", "Selenium 4 + TestNG 7 + Java 11");
        extent.setSystemInfo("Tester", "Shubhank");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));

        log.info("ExtentReports initialized. Report will be saved to: {}", reportPath);
    }
    
    public static void createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        extentTest.set(test);  // Store in ThreadLocal for this thread
        log.debug("ExtentTest created for: {}", testName);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed. Open reports/AutomationReport.html to view.");
        }
    }
}