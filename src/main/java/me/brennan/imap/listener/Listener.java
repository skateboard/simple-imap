package me.brennan.imap.listener;

import me.brennan.imap.model.NewMessage;

import java.util.Locale;

/**
 * @author Brennan / skateboard
 * @since 4/7/2022
 **/
public abstract class Listener {
    private final String from, to;

    public Listener(String from, String to) {
        this.from = from.toLowerCase(Locale.ROOT);
        this.to = to.toLowerCase(Locale.ROOT);
    }

    public abstract void onReceiveMessage(NewMessage message);

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
