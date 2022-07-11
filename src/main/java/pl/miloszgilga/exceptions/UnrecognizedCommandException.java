package pl.miloszgilga.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.messages.EmbedMessageColor;
import pl.miloszgilga.messages.MessageEmbedField;
import static pl.miloszgilga.FranekBot.DEF_PREFIX;


public class UnrecognizedCommandException extends JdaIllegalChatStateException {

    public UnrecognizedCommandException(MessageReceivedEvent event) {
        super(event);
        var embedMessage = new EmbedMessage("ERROR!", "Nieznana komenda", EmbedMessageColor.RED, List.of(
                new MessageEmbedField("Komendy należy używać zgodne ze składnią: ",
                        String.format("`%s<nazwa komendy> [...argumenty]`", DEF_PREFIX), false),
                new MessageEmbedField("Aby uzyskać pełną listę komend wpisz: ",
                        String.format("`%shelp`", DEF_PREFIX), false)
        ));
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return "Nierozpoznana komenda" + getEvent().getAuthor();
    }
}