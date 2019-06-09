package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.SendEmail;
import model.SendEmailAck;
import support.CommonLogger;

/**
 * Created by Dulaj on 2019-06-08.
 */
public final class DataConversionUtil {
    
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    private DataConversionUtil() {
        //        util class with that cannot be instantiated
    }
    
    public static SendEmail convertToSendEmail(Object input) {
        return objectMapper.convertValue(input, SendEmail.class);
    }
    
    public static String getJsonString(SendEmail sendEmail) {
        try {
            return objectMapper.writeValueAsString(sendEmail);
        } catch (JsonProcessingException e) {
            CommonLogger.logErrorMessage(DataConversionUtil.class, e);
            return null;
        }
    }
    
    public static SendEmailAck convertToSendEmailAck(Object input) {
        return objectMapper.convertValue(input, SendEmailAck.class);
    }
    
    public static String getJsonString(SendEmailAck sendEmailAck) {
        try {
            return objectMapper.writeValueAsString(sendEmailAck);
        } catch (JsonProcessingException e) {
            CommonLogger.logErrorMessage(DataConversionUtil.class, e);
            return null;
        }
    }
}
