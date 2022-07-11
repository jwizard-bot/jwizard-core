package pl.miloszgilga.messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class EmbedMessage {

    private final EmbedBuilder builder = new EmbedBuilder();

    private final String title;
    private final String description;
    private final EmbedMessageColor color;
    private List<MessageEmbedField> fields = new ArrayList<>();

    public EmbedMessage(String title, String description, EmbedMessageColor color, List<MessageEmbedField> fields) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.fields = fields;
    }

    public EmbedMessage(String title, String description, EmbedMessageColor color) {
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public MessageEmbed buildMessage() {
        if (!title.equals("")) {
            builder.setTitle(title);
        }
        builder.setDescription(description);
        builder.setColor(Color.decode(color.getColor()));
        fields.forEach(field -> builder.addField(field.getName(), field.getDescription(), field.isIfInline()));
        return builder.build();
    }
}