package app;

import model.SendEmail;
import model.SendEmailAck;
import model.Status;
import support.CommonLogger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class MailServerConnector {
    
    private Session session;
    
    public MailServerConnector(Properties mailConfigurations) {
        session = Session.getInstance(mailConfigurations, new javax.mail.Authenticator() {
        });
    }
    
    public SendEmailAck sendMailToServer(SendEmail sendEmail) {
        
        SendEmailAck emailAck = new SendEmailAck(sendEmail.getRequestID());
        
        try {
            Message message = new MimeMessage(session);
            
            message.setFrom(new InternetAddress(sendEmail.getSender()));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(sendEmail.getRecipient()));
            message.setSubject(sendEmail.getSubject());
            message.setText(sendEmail.getText());
            
            Transport.send(message);
            
            emailAck.setStatus(Status.OK);
            
        } catch (MessagingException e) {
            CommonLogger.logErrorMessage(e);
            emailAck.setStatus(Status.ERROR);
        }
        
        return emailAck;
    }
}
