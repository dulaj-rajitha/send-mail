package app;

import model.SendEmail;
import model.SendEmailAck;
import model.Status;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class MailServerConnectorTest {
    
    private static MailServerConnector connector;
    
    @BeforeClass
    public static void setUp() throws Exception {
        Properties configs = new Properties();
        configs.put("mail.smtp.auth", "false");
        configs.put("mail.smtp.starttls.enable", "true");
        configs.put("mail.smtp.host", "127.0.0.1");
        configs.put("mail.smtp.port", "25");
        connector = new MailServerConnector(configs);
    }
    
    
    @Test
    public void sendMailToServer() {
        
        String requestId = "1";
        
        SendEmail sendEmail = new SendEmail(requestId);
        sendEmail.setSender("test1@test.com");
        sendEmail.setRecipient("test2@test.com");
        sendEmail.setSubject(sendEmail.getRequestID());
        sendEmail.setText(UUID.randomUUID().toString());
        
        SendEmailAck emailAck = connector.sendMailToServer(sendEmail);
        
        Assert.assertNotNull(emailAck);
        Assert.assertEquals(Status.OK, emailAck.getStatus());
        Assert.assertEquals(requestId, emailAck.getRequestID());
    }
}
