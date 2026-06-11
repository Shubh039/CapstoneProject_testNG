package utilities;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import base.BaseTest;


public class TestListener implements ITestListener {
    private static final Logger log = LoggerUtil.getLogger(TestListener.class);
    
    @Override
    public void onStart(ITestContext context) {
        log.info("╔═══════════════════════════════════════════════════════════╗");
        log.info("      STARTING TEST SUITE: {}", context.getName());
        log.info("╚═══════════════════════════════════════════════════════════╝");
        ExtentReportsManager.initReports();
    }
    @Override
    public void onFinish(ITestContext context) {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║     SUITE FINISHED                                     ║");
        log.info("║     Passed : {}  Failed : {}  Skipped: {}                ║",
            context.getPassedTests().size(),
            context.getFailedTests().size(),
            context.getSkippedTests().size());
        log.info("╚════════════════════════════════════════════════════════╝");
        ExtentReportsManager.flushReports();
    }
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        log.info("▶ Starting test: {} — {}", testName, description);

        // Create report entry — uses description from @Test annotation
        ExtentReportsManager.createTest(
            testName,
            description != null && !description.isEmpty()
                ? description
                : testName
        );
    }
    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✅ PASSED: {}", result.getName());
        ExtentReportsManager.getTest()
            .log(Status.PASS,
                "✅ Test PASSED: " + result.getName());
    }
    @Override
    public void onTestFailure(ITestResult result) {
        log.error("❌ FAILED: {}", result.getName());
        log.error("Reason: {}", result.getThrowable().getMessage());
        ExtentTest test = ExtentReportsManager.getTest();

        // Log the failure message
        test.log(Status.FAIL,
            "❌ Test FAILED: " + result.getName());
        test.log(Status.FAIL,
            "Reason: " + result.getThrowable().getMessage());

        // Capture and embed screenshot
        try {
            // Getting the WebDriver from the failing test instance
            BaseTest testInstance = (BaseTest) result.getInstance();
            String base64Screenshot = ScreenShotUtils.takeScreenshotAsBase64(testInstance.getDriver());
          
            // Embedding screenshot directly in HTML report
            if (base64Screenshot != null) {
                test.fail("Screenshot at time of failure:",
                    com.aventstack.extentreports.MediaEntityBuilder
                        .createScreenCaptureFromBase64String(
                            base64Screenshot, result.getName())
                        .build()
                );
            }
            log.info("Screenshot embedded in report for: {}", result.getName());
        } catch (Exception e) {
            log.warn("Could not capture screenshot for report: {}", e.getMessage());
            test.log(Status.WARNING, "Screenshot capture failed: " + e.getMessage());
        }
    }
    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠️ SKIPPED: {}", result.getName());
        ExtentReportsManager.getTest()
            .log(Status.SKIP,
                "⚠️ Test SKIPPED: " + result.getName());
    }
}