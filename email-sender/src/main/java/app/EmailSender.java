package app;

import model.SendEmail;
import model.SendEmailAck;
import support.CommonLogger;
import util.DataConversionUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class EmailSender implements Runnable {
    
    private static final String SERVICE_PORT_KEY = "SERVICE_PORT";
    private static final String DEFAULT_PORT = "4444";
    private static final String MAIL_SERVER_HOST_KEY = "MAIL_SERVER_HOST";
    private static final String DEFAULT_MAIL_SERVER_HOST = "127.0.0.1";
    private static final String MAIL_SERVER_POST_KEY = "MAIL_SERVER_PORT";
    private static final String DEFAULT_MAIL_SERVER_PORT = "25";
    
    private int servicePort;
    private MailServerConnector mailServerConnector;
    private boolean running;
    
    public EmailSender() {
        servicePort = Integer.parseInt(System.getProperty(SERVICE_PORT_KEY, DEFAULT_PORT));
        
        String mailServerHost = System.getProperty(MAIL_SERVER_HOST_KEY, DEFAULT_MAIL_SERVER_HOST);
        String mailServerPort = System.getProperty(MAIL_SERVER_POST_KEY, DEFAULT_MAIL_SERVER_PORT);
        
        Properties mailConfigurations = new Properties();
        mailConfigurations.put("mail.smtp.auth", "false");
        mailConfigurations.put("mail.smtp.starttls.enable", "true");
        mailConfigurations.put("mail.smtp.host", mailServerHost);
        mailConfigurations.put("mail.smtp.port", mailServerPort);
        
        mailServerConnector = new MailServerConnector(mailConfigurations);
        
        running = true;
    }
    
    private void startServer() {
        
        CommonLogger.logInfoMessage("starting mail sender service");
        CommonLogger.logInfoMessage("starting listening on port : " + servicePort);
        
        
        while (running) {
            try (ServerSocket serverSocket = new ServerSocket(servicePort);
                 Socket socket = serverSocket.accept();
                 ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())
            ) {
                
                CommonLogger.logInfoMessage("receiving a client mail");
                
                //  read the mail
                Object clientData = inputStream.readObject();
                SendEmail inputMail = DataConversionUtil.convertToSendEmail(clientData);
                
                // send the mail through fake SMTP server
                SendEmailAck mailServerAck = mailServerConnector.sendMailToServer(inputMail);
                
                //  write ack to client
                
                outputStream.writeObject(mailServerAck);
                
            } catch (IOException e) {
                CommonLogger.logErrorMessage(e);
            } catch (ClassNotFoundException e) {
                CommonLogger.logErrorMessage(e);
            }
        }
    }
    
    public void shutDown() {
        CommonLogger.logInfoMessage("shutting down mail sender service");
        running = false;
    }
    
    @Override
    public void run() {
        startServer();
    }
}
