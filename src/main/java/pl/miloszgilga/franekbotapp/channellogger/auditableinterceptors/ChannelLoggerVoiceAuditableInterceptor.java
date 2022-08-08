/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ChannelLoggerVoiceAuditableInterceptor.java
 * Last modified: 08/08/2022, 22:23
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

package pl.miloszgilga.franekbotapp.channellogger.auditableinterceptors;

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import pl.miloszgilga.franekbotapp.channellogger.IBasicAuditableInterceptor;


public final class ChannelLoggerVoiceAuditableInterceptor extends ListenerAdapter implements IBasicAuditableInterceptor {

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        super.onGuildVoiceJoin(event);
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        super.onGuildVoiceMove(event);
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        super.onGuildVoiceLeave(event);
    }

    @Override
    public void onGuildVoiceStream(@NotNull GuildVoiceStreamEvent event) {
        super.onGuildVoiceStream(event);
    }

    @Override
    public void onGuildVoiceVideo(@NotNull GuildVoiceVideoEvent event) {
        super.onGuildVoiceVideo(event);
    }
}