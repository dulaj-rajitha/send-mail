package app;

import model.SendEmail;
import model.SendEmailAck;
import model.Status;
import support.CommonLogger;
import util.DataConversionUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class MailClient {
    
    static final String DEFAULT_REQUEST_COUNT = "4";
    //    configs
    private static final String THREAD_COUNT_KEY = "THREAD_COUNT";
    private static final String DEFAULT_THREADS = "2";
    private static final String REQUEST_COUNT_KEY = "REQUEST_COUNT";
    
    private static final String SERVICE_HOST_KEY = "SERVICE_HOST";
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String SERVICE_PORT_KEY = "SERVICE_PORT";
    private static final String DEFAULT_PORT = "4444";
    
    private static final String SENDER_ADDRESS = "sender@test.com";
    private static final String RECIPIENT_ADDRESS = "recipient@test.com";
    
    private int servicePort;
    private String serviceHost;
    private ScheduledExecutorService executorService;
    private int requestCount;
    
    public MailClient() {
        
        int threadCount = Integer.parseInt(System.getProperty(THREAD_COUNT_KEY, DEFAULT_THREADS));
        executorService = Executors.newScheduledThreadPool(threadCount);
        serviceHost = System.getProperty(SERVICE_HOST_KEY, DEFAULT_HOST);
        servicePort = Integer.parseInt(System.getProperty(SERVICE_PORT_KEY, DEFAULT_PORT));
        
        requestCount = Integer.parseInt(System.getProperty(REQUEST_COUNT_KEY, DEFAULT_REQUEST_COUNT));
        
    }
    
    public List<SendEmailAck> sendMails() {
        List<SendEmailAck> responses = IntStream.rangeClosed(0, requestCount).boxed()
                .map(String::valueOf)
                .map(this::generateSendMail)
                .map(this::sendMail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        long successCount = responses.stream()
                .filter(ack -> !Objects.equals(Status.OK, ack.getStatus()))
                .count();
        
        CommonLogger.logInfoMessage(successCount + "/" + requestCount + " requests succeeded");
        
        return responses;
    }
    
    
    private SendEmailAck sendMail(SendEmail mail) {
        try {
            return executorService
                    .submit(() -> {
                        
                        try (Socket socket = new Socket(serviceHost, servicePort);
                             ObjectOutputStream out =
                                     new ObjectOutputStream(socket.getOutputStream());
                             DataInputStream in = new DataInputStream(socket.getInputStream())
                        ) {
                            //  send the mail
                            out.writeObject(DataConversionUtil.getJsonString(mail));
                            //  get the ack
                            return DataConversionUtil.convertToSendEmailAck(in.readUTF());
                            
                        } catch (IOException e) {
                            CommonLogger.logErrorMessage(e);
                        }
                        return null;
                    })
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            CommonLogger.logErrorMessage(e);
        } catch (ExecutionException e) {
            CommonLogger.logErrorMessage(e);
        }
        
        return null;
    }
    
    private SendEmail generateSendMail(String requestID) {
        SendEmail sendEmail = new SendEmail(requestID);
        sendEmail.setSubject(requestID);
        sendEmail.setText(requestID);
        sendEmail.setSender(SENDER_ADDRESS);
        sendEmail.setRecipient(RECIPIENT_ADDRESS);
        
        return sendEmail;
    }
    
    
}
