package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class SendEmail implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String requestID;
    private String sender;
    private String recipient;
    private String subject;
    private String text;
    
    
    public SendEmail() {
        //        default construct
    }
    
    public String getRequestID() {
        return requestID;
    }
    
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String email) {
        this.recipient = email;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        SendEmail sendEmail = (SendEmail) o;
        
        return Objects.equals(requestID, sendEmail.getRequestID());
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(requestID);
    }
}
