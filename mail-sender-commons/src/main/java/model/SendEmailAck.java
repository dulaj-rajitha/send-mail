package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class SendEmailAck implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String requestID;
    private Status status;
    
    public SendEmailAck() {
        this.status = Status.ERROR;
    }
    
    public SendEmailAck(String requestID) {
        this();
        this.requestID = requestID;
    }
    
    public SendEmailAck(String requestID, Status status) {
        this.requestID = requestID;
        this.status = status;
    }
    
    public String getRequestID() {
        return requestID;
    }
    
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
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
    
    @Override
    public String toString() {
        return "SendEmailAck{" +
                "requestID='" + requestID + '\'' +
                ", status=" + status +
                '}';
    }
}
