/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: EmbedMessage.java
 * Last modified: 10/07/2022, 15:33
 * Project name: franek-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

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