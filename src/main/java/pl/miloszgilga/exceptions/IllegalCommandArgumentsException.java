package pl.miloszgilga.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.messages.EmbedMessageColor;
import pl.miloszgilga.messages.MessageEmbedField;


public class IllegalCommandArgumentsException extends JdaIllegalChatStateException {

    public IllegalCommandArgumentsException(MessageReceivedEvent event, String commandSyntax) {
        super(event);
        var embedMessage = new EmbedMessage("ERROR!", "Nieprawidłowe argumenty komendy", EmbedMessageColor.RED, List.of(
                new MessageEmbedField("Komendy należy używać zgodne ze składnią: ", commandSyntax, false)
        ));
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return "Błędne argumenty komendy" + getEvent().getAuthor();
    }

}