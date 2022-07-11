package pl.miloszgilga.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.messages.EmbedMessageColor;


public class AttemptToRevoteSkippingSongException extends JdaIllegalChatStateException {

    public AttemptToRevoteSkippingSongException(MessageReceivedEvent event) {
        super(event);
        var embedMessage = new EmbedMessage("UWAGA!", String.format(
                "Hej **%s** W głosowaniu na pominięcie piosenki możesz wziąć udział tylko raz.", event.getAuthor().getName()),
                EmbedMessageColor.ORANGE
        );
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return "Próba ponownego zagłosowania" + getEvent().getAuthor();
    }
}