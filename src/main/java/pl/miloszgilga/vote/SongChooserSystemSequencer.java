/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SongChooserSystemSequencer.java
 * Last modified: 18/06/2023, 17:54
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

package pl.miloszgilga.vote;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.UnicodeEmoji;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class SongChooserSystemSequencer implements IVoteSequencer {

    private final List<AudioTrack> loadedTracks;
    private final CommandEventWrapper event;
    private final RemotePropertyHandler handler;
    private final EmbedMessageBuilder builder;
    private final BotConfiguration config;
    private final Consumer<AudioTrack> onSelectTrack;

    private final AtomicInteger selectedIndex = new AtomicInteger();

    int elapsedTimeInSec;
    int countOfMaxTracks;
    boolean isRandom;

    private Function<Message, RestAction<List<Void>>> emojisFunctor;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SongChooserSystemSequencer(List<AudioTrack> loadedTracks, SongChooserConfigData data) {
        this.event = data.event();
        this.config = data.config();
        this.handler = data.handler();
        this.builder = data.builder();
        this.onSelectTrack = data.onSelectTrack();
        this.loadedTracks = trimSongsList(loadedTracks);
        initRemoteElements();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void initializeAndStart() {
        JDALog.info(log, event, "Initialized voting for select song from results list: %s", loadedTracks);

        final StringBuilder stringBuilder = new StringBuilder();
        final ResLocaleSet resultAfter = isRandom ? ResLocaleSet.RANDOM_RESULT_MESS : ResLocaleSet.FIRST_RESULT_MESS;

        final String rawMessage = config.getLocaleText(ResLocaleSet.SELECT_SONG_SEQUENCER_MESS, Map.of(
            "resultsFound", loadedTracks.size(),
            "elapsedTime", elapsedTimeInSec,
            "afterTimeResult", config.getLocaleText(resultAfter)
        ));
        stringBuilder.append(rawMessage);
        stringBuilder.append("\n\n");

        for (int i = 0; i < loadedTracks.size(); i++) {
            stringBuilder.append(String.format("`%d`", i));
            stringBuilder.append(StringUtils.SPACE);
            stringBuilder.append(Utilities.getRichTrackTitle(loadedTracks.get(i)));
            stringBuilder.append('\n');
        }
        final String buildedMessage = stringBuilder.toString();
        final MessageEmbed messageEmbed = builder.createMessage(buildedMessage, event);

        if (event.isFromSlashCommand() && !event.getSlashCommandEvent().getHook().isExpired()) {
            event.getSlashCommandEvent().getHook().sendMessageEmbeds(messageEmbed)
                .queue(message -> emojisFunctor.apply(message).queue(ignored -> fabricateEventWaiter(message)));
            return;
        }
        event.getTextChannel().sendMessageEmbeds(messageEmbed)
            .queue(message -> emojisFunctor.apply(message).queue(ignored -> fabricateEventWaiter(message)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void fabricateEventWaiter(Message message) {
        config.getEventWaiter().waitForEvent(
            GuildMessageReactionAddEvent.class,
            e -> onAfterSelectPredicate(e, message),
            e -> onSelectTrack.accept(loadedTracks.get(selectedIndex.get())),
            elapsedTimeInSec,
            TimeUnit.SECONDS,
            () -> onAfterTimeoutVotingRunnable(message)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean onAfterSelectPredicate(GuildMessageReactionAddEvent e, Message message) {
        if (!e.getMessageId().equals(message.getId())) return false;
        if (e.getUser().isBot()) return false;

        final MessageReaction.ReactionEmote emote = e.getReactionEmote();
        if (!e.getUser().getId().equals(event.getAuthor().getId())) return false;
        if (!emote.isEmoji()) return false;

        final Optional<UnicodeEmoji> selectedEmoji = UnicodeEmoji.getNumbers(countOfMaxTracks).stream()
            .filter(em -> em.getCode().equals(emote.getEmoji()))
            .findFirst();

        if (selectedEmoji.isPresent()) {
            final int index = selectedEmoji.get().getIndex();
            selectedIndex.set(index);
            message.clearReactions().queue();
            JDALog.info(log, event, "Selecting track was ended successfully. Selected track (%s): %s", selectedIndex,
                loadedTracks.get(index));
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void onAfterTimeoutVotingRunnable(Message message) {
        int selectedIndex;
        if (isRandom) {
            selectedIndex = RandomUtils.nextInt(0, loadedTracks.size());
        } else {
            selectedIndex = 0;
        }
        JDALog.info(log, event, "Selecting track from results is ended. Selected track (%s): %s", selectedIndex,
            loadedTracks.get(selectedIndex));
        message.clearReactions().queue();
        onSelectTrack.accept(loadedTracks.get(selectedIndex));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<AudioTrack> trimSongsList(List<AudioTrack> loadedTracks) {
        this.countOfMaxTracks = handler.getPossibleRemoteProperty(RemoteProperty.R_SONG_CHOOSER_COUNT,
            event.getGuild(), Integer.class);
        return loadedTracks.subList(0, countOfMaxTracks);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initRemoteElements() {
        this.elapsedTimeInSec = handler.getPossibleRemoteProperty(RemoteProperty.R_SONG_CHOOSER_SELECT_TIME,
            event.getGuild(), Integer.class);
        this.isRandom = handler.getPossibleRemoteProperty(RemoteProperty.R_SONG_CHOOSER_RANDOM_ACTIVE,
            event.getGuild(), Boolean.class);
        this.emojisFunctor = message -> RestAction.allOf(
            UnicodeEmoji.getNumbers(countOfMaxTracks).stream().map(n -> message.addReaction(n.getCode())).toList()
        );
    }
}
