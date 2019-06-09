package app;

import model.SendEmail;
import model.SendEmailAck;
import support.CommonLogger;
import util.DataConversionUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

/**
 * Created by Dulaj on 2019-06-09.
 */
public class EmailSenderThread extends Thread {
    
    private Socket socket;
    private MailServerConnector mailServerConnector;
    
    public EmailSenderThread(Socket socket, Properties mailConfigurations) {
        super("EmailSenderThread");
        this.socket = socket;
        mailServerConnector = new MailServerConnector(mailConfigurations);
    }
    
    @Override
    public void run() {
        
        try (
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            
            CommonLogger.logInfoMessage(this, "receiving a client mail");
            
            //  read the mail
            Object clientData = inputStream.readObject();
            SendEmail inputMail = DataConversionUtil.convertToSendEmail(clientData);
            
            // send the mail through fake SMTP server
            SendEmailAck mailServerAck = mailServerConnector.sendMailToServer(inputMail);
            
            //  write ack to client
            
            outputStream.writeObject(mailServerAck);
            socket.close();
            
        } catch (IOException | ClassNotFoundException e) {
            CommonLogger.logErrorMessage(e);
        }
    }
}
