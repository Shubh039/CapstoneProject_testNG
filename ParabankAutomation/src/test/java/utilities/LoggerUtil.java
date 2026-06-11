package utilities;

import org.apache.logging.log4j.*;
public class LoggerUtil {
    private LoggerUtil() {}
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}