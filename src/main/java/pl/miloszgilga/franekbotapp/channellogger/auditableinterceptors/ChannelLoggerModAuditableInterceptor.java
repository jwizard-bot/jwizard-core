/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ChannelLoggerModAuditableInterceptor.java
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
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;

import pl.miloszgilga.franekbotapp.channellogger.IBasicAuditableInterceptor;


public final class ChannelLoggerModAuditableInterceptor extends ListenerAdapter implements IBasicAuditableInterceptor {

    @Override
    public void onEmojiAdded(@NotNull EmojiAddedEvent event) {
        super.onEmojiAdded(event);
    }

    @Override
    public void onEmojiRemoved(@NotNull EmojiRemovedEvent event) {
        super.onEmojiRemoved(event);
    }

    @Override
    public void onRoleCreate(@NotNull RoleCreateEvent event) {
        super.onRoleCreate(event);
    }

    @Override
    public void onRoleUpdateName(@NotNull RoleUpdateNameEvent event) {
        super.onRoleUpdateName(event);
    }

    @Override
    public void onRoleUpdateColor(@NotNull RoleUpdateColorEvent event) {
        super.onRoleUpdateColor(event);
    }

    @Override
    public void onRoleDelete(@NotNull RoleDeleteEvent event) {
        super.onRoleDelete(event);
    }

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        super.onChannelCreate(event);
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        super.onChannelDelete(event);
    }

    @Override
    public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
        super.onChannelUpdateName(event);
    }

    @Override
    public void onChannelUpdateNSFW(@NotNull ChannelUpdateNSFWEvent event) {
        super.onChannelUpdateNSFW(event);
    }
}