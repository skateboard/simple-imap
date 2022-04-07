package me.brennan.imap;

import com.sun.mail.imap.IdleManager;
import me.brennan.imap.listener.Listener;
import me.brennan.imap.model.NewMessage;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Brennan / skateboard
 * @since 4/7/2022
 **/
public class Imap {
    private final List<Listener> listeners = new LinkedList<>();

    public Imap(String host, String username, String password, boolean verbose) throws MessagingException, IOException {
        final Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.put("mail.pop3.ssl.enable", "true");
        props.setProperty("mail.imaps.usesocketchannels", "true");

        final var session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");

        store.connect(host,username, password);
        Folder folder = store.getFolder("Inbox");
        folder.open(Folder.READ_WRITE);

        if (verbose) {
            System.out.println("Connected to IMAP server");
        }

        ExecutorService es = Executors.newCachedThreadPool();
        final var idleManager = new IdleManager(session, es);

        folder.addMessageCountListener(new MessageCountAdapter() {
            public void messagesAdded(MessageCountEvent ev) {
                try {
                    Folder folder = (Folder) ev.getSource();
                    var messages = ev.getMessages();

                    for (var message : messages) {
                        var from = "unknown";
                        if (message.getFrom().length >= 1) {
                            from = message.getFrom()[0].toString();
                            final String[] split = from.split("<");
                            from = split[1].trim().replaceAll(">", "");
                        }

                        var to = "unknown";
                        if (message.getRecipients(Message.RecipientType.TO).length >= 1) {
                            to = message.getRecipients(Message.RecipientType.TO)[0].toString();
                        }
                        var subject = message.getSubject();

                        final var listener = getListener(to, from);

                        if (listener != null) {
                            listener.onReceiveMessage(new NewMessage(to, from, subject, message.getContent().toString()));
                        } else {
                            System.out.println("No listener for " + to + " " + from);
                        }

                        message.setFlag(Flags.Flag.SEEN, true);
                    }

                    idleManager.watch(folder); // keep watching for new messages
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        idleManager.watch(folder);

    }

    public Imap(String host, String username, String password) throws MessagingException, IOException {
        this(host, username, password, false);
    }

    public Listener getListener(String to, String from) {
        for (var listener : listeners) {
            if (listener.getTo().equals(to) && listener.getFrom().equals(from))
                return listener;
        }

        return null;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
