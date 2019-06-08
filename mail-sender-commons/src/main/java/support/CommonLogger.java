package support;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dulaj on 2019-06-08.
 */
public final class CommonLogger {
    private static final Logger LOGGER;
    
    static {
        LOGGER = LoggerFactory.getLogger(CommonLogger.class);
    }
    
    private CommonLogger() {
    }
    
    private static Logger getLogger(Object origin) {
        if (origin instanceof Class<?>) {
            return LoggerFactory.getLogger((Class<?>) origin);
        } else {
            return LoggerFactory.getLogger(origin.getClass());
        }
    }
    
    public static void logInfoMessage(String message) {
        LOGGER.info(message);
    }
    
    public static void logInfoMessage(Object origin, String message, Object... args) {
        getLogger(origin).info(message, args);
    }
    
    
    public static void logErrorMessage(Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);
    }
    
    public static void logErrorMessage(Object origin, Throwable throwable) {
        getLogger(origin).error(throwable.getMessage(), throwable);
    }
    
    public static void logErrorMessage(Object origin, String message, Throwable throwable) {
        getLogger(origin).error(message, throwable);
    }
    
}
