package support;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class CommonLogger {
    
    public static void logInfoMessage(String message) {
        System.out.println(message);
    }
    
    public static void logErrorMessage(Throwable throwable) {
        System.err.println(throwable.getMessage());
    }
}
