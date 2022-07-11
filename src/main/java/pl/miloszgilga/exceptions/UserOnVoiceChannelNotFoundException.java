package pl.miloszgilga.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.messages.EmbedMessageColor;


public class UserOnVoiceChannelNotFoundException extends JdaIllegalChatStateException {

    public UserOnVoiceChannelNotFoundException(MessageReceivedEvent event, String description) {
        super(event);
        var embedMessage = new EmbedMessage("UWAGA!", description, EmbedMessageColor.ORANGE);
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return "Użytkownik nie znajduje się na kanale głosowym" + getEvent().getAuthor();
    }

}