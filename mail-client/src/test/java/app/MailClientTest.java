package app;

import model.SendEmailAck;
import model.Status;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Objects;

import static app.MailClient.DEFAULT_REQUEST_COUNT;

/**
 * Created by Dulaj on 2019-06-08.
 */
public class MailClientTest {
    @Test
    public void sendMails() throws Exception {
        MailClient mailClient = new MailClient();
        Collection<SendEmailAck> sendEmailAcks = mailClient.sendMails();
        
        Assert.assertNotNull(sendEmailAcks);
        int requstCount = Integer.parseInt(DEFAULT_REQUEST_COUNT);
        Assert.assertEquals(requstCount, sendEmailAcks.size());
        
        long errorCount = sendEmailAcks.stream()
                .filter(Objects::nonNull)
                .filter(ack -> Objects.equals(Status.ERROR, ack.getStatus()))
                .count();
        Assert.assertEquals(0, errorCount);
    }
}
