package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.SendEmail;
import model.SendEmailAck;
import support.CommonLogger;

import java.io.IOException;

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
    
    public static SendEmail convertToSendEmail(String input) {
        try {
            return objectMapper.readValue(input, SendEmail.class);
        } catch (IOException e) {
            CommonLogger.logErrorMessage(e);
            return null;
        }
    }
    
    public static String getJsonString(SendEmail sendEmail) {
        try {
            return objectMapper.writeValueAsString(sendEmail);
        } catch (JsonProcessingException e) {
            CommonLogger.logErrorMessage(e);
            return null;
        }
    }
    
    public static SendEmailAck convertToSendEmailAck(String input) {
        try {
            return objectMapper.readValue(input, SendEmailAck.class);
        } catch (IOException e) {
            CommonLogger.logErrorMessage(e);
            return null;
        }
    }
    
    public static String getJsonString(SendEmailAck sendEmailAck) {
        try {
            return objectMapper.writeValueAsString(sendEmailAck);
        } catch (JsonProcessingException e) {
            CommonLogger.logErrorMessage(e);
            return null;
        }
    }
}
