/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedMessageBuilder.java
 * Last modified: 05/03/2023, 23:38
 * Project name: jwizard-discord-bot
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

package pl.miloszgilga.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import org.springframework.stereotype.Component;

import java.awt.*;

import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class EmbedMessageBuilder {

    private final BotConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmbedMessageBuilder(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(EventWrapper wrapper, String message) {
        return new EmbedBuilder()
            .setAuthor(wrapper.authorTag(), null, wrapper.authorAvatarUrl())
            .setTitle(config.getLocaleText(LocaleSet.ERROR_HEADER))
            .setDescription(message)
            .setColor(Color.decode(EmbedColor.PURPLE.getHex()))
            .build();
    }
}
