/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotMuteCommandListener.java
 * Last modified: 16/05/2023, 18:47
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.miloszgilga.listener;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;

import java.util.Objects;

import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.IEnumerableLocaleSet;
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

    private void stopPlayingContentAndFreeze(GuildVoiceMuteEvent event) {
        if (!event.getMember().getUser().isBot()) return;

        final Member botMember = event.getGuild().getSelfMember();
        if (Objects.isNull(botMember.getVoiceState())) return;

        final MusicManager musicManager = playerManager.getMusicManager(botMember.getGuild());
        if (Objects.isNull(musicManager)) return;

        IEnumerableLocaleSet message = ResLocaleSet.RESUME_TRACK_ON_FORCE_UNMUTE_MESS;
        if (botMember.getVoiceState().isMuted()) {
            message = ResLocaleSet.PAUSE_TRACK_ON_FORCE_MUTE_MESS;
            musicManager.getAudioPlayer().setPaused(true);
        } else {
            musicManager.getAudioPlayer().setPaused(false);
        }
        final MessageEmbed messageEmbed = embedBuilder.createMessage(message, event.getGuild());
        musicManager.getTrackScheduler().getDeliveryEvent().getTextChannel().sendMessageEmbeds(messageEmbed).queue();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGuildVoiceMute(GuildVoiceMuteEvent event) { stopPlayingContentAndFreeze(event); }
}
