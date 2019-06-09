package app;

import support.CommonLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class EmailSender {
    
    private static final String SERVICE_PORT_KEY = "SERVICE_PORT";
    private static final String DEFAULT_PORT = "4444";
    private static final String MAIL_SERVER_HOST_KEY = "MAIL_SERVER_HOST";
    private static final String DEFAULT_MAIL_SERVER_HOST = "127.0.0.1";
    private static final String MAIL_SERVER_POST_KEY = "MAIL_SERVER_PORT";
    private static final String DEFAULT_MAIL_SERVER_PORT = "25";
    
    private int servicePort;
    private Properties mailConfigurations;
    private boolean running;
    
    public EmailSender() {
        servicePort = Integer.parseInt(System.getProperty(SERVICE_PORT_KEY, DEFAULT_PORT));
        
        String mailServerHost = System.getProperty(MAIL_SERVER_HOST_KEY, DEFAULT_MAIL_SERVER_HOST);
        String mailServerPort = System.getProperty(MAIL_SERVER_POST_KEY, DEFAULT_MAIL_SERVER_PORT);
        
        mailConfigurations = new Properties();
        mailConfigurations.put("mail.smtp.auth", "false");
        mailConfigurations.put("mail.smtp.starttls.enable", "true");
        mailConfigurations.put("mail.smtp.host", mailServerHost);
        mailConfigurations.put("mail.smtp.port", mailServerPort);
        
        running = true;
    }
    
    public static void main(String[] args) {
        EmailSender emailSender = new EmailSender();
        emailSender.startServer();
    }
    
    private void startServer() {
        
        CommonLogger.logInfoMessage(this, "starting mail sender service");
        CommonLogger.logInfoMessage(this, "starting listening on port : " + servicePort);
        
        try (ServerSocket serverSocket = new ServerSocket(servicePort)) {
            while (running) {
                CommonLogger.logInfoMessage(this, "creating new thread for send the mail with the socket lister");
                new EmailSenderThread(serverSocket.accept(), mailConfigurations).start();
            }
        } catch (IOException e) {
            CommonLogger.logErrorMessage(this, "Could not listen on port " + servicePort, e);
            System.exit(-1);
        }
    }
    
    public void shutDown() {
        CommonLogger.logInfoMessage(this, "shutting down mail sender service");
        running = false;
    }
    
}
