import me.brennan.imap.Imap;
import me.brennan.imap.listener.Listener;
import me.brennan.imap.model.NewMessage;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brennan / skateboard
 * @since 4/7/2022
 **/
public class ImapTest {

    @Test
    public void testImap() throws MessagingException, IOException, InterruptedException {
        final Imap imap = new Imap(System.getenv("IMAP_HOST"), System.getenv("IMAP_USERNAME"), System.getenv("IMAP_PASSWORD"));

        final AtomicBoolean done = new AtomicBoolean(false);

        imap.addListener(new Listener(System.getenv("IMAP_FROM"), System.getenv("IMAP_TO")) {
            @Override
            public void onReceiveMessage(NewMessage message) {
                System.out.println(message.body());
                done.set(true);
            }
        });

        while (!done.get()) {
            System.out.println("Waiting for email...");
            TimeUnit.SECONDS.sleep(2);
        }
    }

}
