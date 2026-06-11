package utilities;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
	private static Properties properties;
	
	static {
		try {
			FileInputStream file = new FileInputStream("src/test/resources/config.properties");
			properties = new Properties();
			properties.load(file);
			file.close();
		} catch(IOException e) {
			throw new RuntimeException(
	                "config.properties file not found! " +
	                "Check path: src/test/resources/config/config.properties", e
	            );
		}
		
	}
	public static String get(String key) {
		String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(
                "Property '" + key + "' not found in config.properties"
            );
        }
        return value;
	}
}
