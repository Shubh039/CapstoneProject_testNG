package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;

public class ScreenShotUtils {
    // Logger
    private static final Logger log = LoggerUtil.getLogger(ScreenShotUtils.class);
    
	public static void takeScreenshot(WebDriver driver,String testName) {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE); // storing in a temporary file
		
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		String destination = ConfigReader.get("screenshotPath") + testName + "_" + timestamp + ".png";
		try {
			Path screenshotDir = Paths.get(ConfigReader.get("screenshotPath"));
			Files.createDirectories(screenshotDir);
			Files.copy(source.toPath(), Paths.get(destination));
            log.info("Screenshot saved: {}", destination);
            
		} catch (IOException e){
            System.out.println("Failed to save screenshot: " + e.getMessage());
            log.error("Failed to save screenshot: {}", e.getMessage());
		}
	}
	
	// for extent reports
    public static String takeScreenshotAsBase64(WebDriver driver) {
        try {
            String base64 = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BASE64);
            log.info("Screenshot captured as Base64 for ExtentReports");
            return base64;

        } catch (Exception e) {
            log.error("Failed to capture Base64 screenshot: {}", e.getMessage());
            return null;
        }
    }
}

