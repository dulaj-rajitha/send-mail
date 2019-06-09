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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class EmailSender {
    
    private static final String THREAD_COUNT_KEY = "THREAD_COUNT";
    private static final String DEFAULT_THREADS = "4";
    private static final String SERVICE_PORT_KEY = "SERVICE_PORT";
    private static final String DEFAULT_PORT = "4444";
    private static final String MAIL_SERVER_HOST_KEY = "MAIL_SERVER_HOST";
    private static final String DEFAULT_MAIL_SERVER_HOST = "127.0.0.1";
    private static final String MAIL_SERVER_POST_KEY = "MAIL_SERVER_PORT";
    private static final String DEFAULT_MAIL_SERVER_PORT = "25";
    
    private int servicePort;
    private MailServerConnector mailServerConnector;
    private boolean running;
    private ExecutorService executorService;
    
    public EmailSender() {
        
        //  note: increasing this will increase the load on SMTP server
        int threadCount = Integer.parseInt(System.getProperty(THREAD_COUNT_KEY, DEFAULT_THREADS));
        executorService = Executors.newFixedThreadPool(threadCount);
        servicePort = Integer.parseInt(System.getProperty(SERVICE_PORT_KEY, DEFAULT_PORT));
        
        String mailServerHost = System.getProperty(MAIL_SERVER_HOST_KEY, DEFAULT_MAIL_SERVER_HOST);
        String mailServerPort = System.getProperty(MAIL_SERVER_POST_KEY, DEFAULT_MAIL_SERVER_PORT);

        //        todo: these can be get form environment variables as well
        Properties mailConfigurations = new Properties();
        mailConfigurations.put("mail.smtp.auth", "false");
        mailConfigurations.put("mail.smtp.starttls.enable", "true");
        mailConfigurations.put("mail.smtp.host", mailServerHost);
        mailConfigurations.put("mail.smtp.port", mailServerPort);
        
        mailServerConnector = new MailServerConnector(mailConfigurations);
        
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
                Socket socket = serverSocket.accept();
                executorService.execute(() -> readSocketAndSendMail(socket));
            }
        } catch (IOException e) {
            CommonLogger.logErrorMessage(this, "Could not listen on port " + servicePort, e);
            System.exit(-1);
        }
    }
    
    public void shutDown() {
        CommonLogger.logInfoMessage(this, "shutting down mail sender service");
        running = false;
        executorService.shutdown();
    }
    
    private void readSocketAndSendMail(Socket socket) {
        
        try (
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            
            CommonLogger.logInfoMessage(this, "receiving a client mail");
            
            //  read the mail
            Object clientData = inputStream.readObject();
            SendEmail inputMail = DataConversionUtil.convertToSendEmail(clientData);
            
            //  todo: can validate the mail before send to SMTP
            
            // send the mail through fake SMTP server
            SendEmailAck mailServerAck = mailServerConnector.sendMailToServer(inputMail);
            
            //  write ack to client
            
            outputStream.writeObject(mailServerAck);
            socket.close();
            
        } catch (IOException | ClassNotFoundException e) {
            CommonLogger.logErrorMessage(this, e);
        }
    }
    
}
