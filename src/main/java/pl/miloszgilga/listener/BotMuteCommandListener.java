/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotMuteCommandListener.java
 * Last modified: 12/03/2023, 19:03
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

package pl.miloszgilga.listener;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;

import java.util.Objects;

import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableListenerLazyService
public class BotMuteCommandListener extends AbstractListenerAdapter {

    private final PlayerManager playerManager;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BotMuteCommandListener(BotConfiguration config, EmbedMessageBuilder embedBuilder, PlayerManager playerManager) {
        super(config, embedBuilder);
        this.playerManager = playerManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        final Member botMember = event.getGuild().getSelfMember();
        if (Objects.isNull(botMember.getVoiceState())) return;

        final MusicManager musicManager = playerManager.getMusicManager(botMember.getGuild());
        if (Objects.isNull(musicManager)) return;

        LocaleSet message = LocaleSet.RESUME_TRACK_ON_FORCE_UNMUTE_MESS;
        if (botMember.getVoiceState().isMuted()) {
            message = LocaleSet.PAUSE_TRACK_ON_FORCE_MUTE_MESS;
            musicManager.getAudioPlayer().setPaused(true);
        } else {
            musicManager.getAudioPlayer().setPaused(false);
        }
        final MessageEmbed messageEmbed = embedBuilder.createMessage(message);
        musicManager.getTrackScheduler().getDeliveryEvent().textChannel().sendMessageEmbeds(messageEmbed).queue();
    }
}
