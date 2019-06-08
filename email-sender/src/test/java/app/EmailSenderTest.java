package app;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class EmailSenderTest {
    
    private static EmailSender emailSender;
    
    @BeforeClass
    public static void setUp() throws Exception {
        
        emailSender = new EmailSender();
    }
    
    @Test
    public void testSender() throws Exception {
        Thread senderThread = new Thread(emailSender);
        senderThread.start();
        
    }
    
    public static void main(String[] args) {
        EmailSender emailSender = new EmailSender();
        emailSender.run();
    }
}