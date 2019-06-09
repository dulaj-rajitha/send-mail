package app;

import model.SendEmail;
import model.SendEmailAck;
import support.CommonLogger;
import util.DataConversionUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class MailClient {
    
    static final String DEFAULT_REQUEST_COUNT = "16";
    //    configs
    private static final String THREAD_COUNT_KEY = "THREAD_COUNT";
    private static final String DEFAULT_THREADS = "8";
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
    
    public static void main(String[] args) {
        MailClient mailClient = new MailClient();
        Collection<SendEmailAck> sendEmailAcks = mailClient.sendMailsAsync();
        CommonLogger.logInfoMessage("got acknowledgements: " + sendEmailAcks);
        System.exit(0);
    }
    
    public List<SendEmailAck> sendMailsAsync() {
        List<SendEmail> mails = IntStream.range(0, requestCount).boxed()
                .map(String::valueOf)
                .map(this::generateSendMail)
                .collect(Collectors.toList());
        
        return mails.parallelStream()
                .map(this::sendMailAsync)
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    private CompletableFuture<SendEmailAck> sendMailAsync(SendEmail mail) {
        return CompletableFuture.supplyAsync(() -> {
            try (Socket socket = new Socket(serviceHost, servicePort);
                 ObjectOutputStream out =
                         new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                CommonLogger.logInfoMessage(this, "client sending mail :" + mail.getRequestID() + " using thread " + Thread.currentThread().getName());
                //  send the mail
                out.writeObject(mail);
                //  get the ack
                SendEmailAck ack = DataConversionUtil.convertToSendEmailAck(in.readObject());
                CommonLogger.logInfoMessage(this, "got the ack for req: " + ack.getRequestID() + " as: " + ack.getStatus());
                return ack;
            } catch (IOException | ClassNotFoundException e) {
                CommonLogger.logErrorMessage(this, e);
            }
            return null;
        }, executorService);
    }
    
    
    private SendEmail generateSendMail(String requestID) {
        SendEmail sendEmail = new SendEmail();
        sendEmail.setRequestID(requestID);
        sendEmail.setSubject(requestID);
        sendEmail.setText(requestID);
        sendEmail.setSender(SENDER_ADDRESS);
        sendEmail.setRecipient(RECIPIENT_ADDRESS);
        
        return sendEmail;
    }
    
}
