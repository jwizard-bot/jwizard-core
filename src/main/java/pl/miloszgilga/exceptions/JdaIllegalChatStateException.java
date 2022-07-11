package pl.miloszgilga.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class JdaIllegalChatStateException extends RuntimeException {

    private final MessageReceivedEvent event;

    public JdaIllegalChatStateException(MessageReceivedEvent event) {
        this.event = event;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }
}