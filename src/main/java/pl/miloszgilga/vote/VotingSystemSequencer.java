/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VotingSystemSequencer.java
 * Last modified: 17/05/2023, 01:30
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

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.UnicodeEmoji;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.AudioPlayerException.UserOnVoiceChannelWithBotNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class VotingSystemSequencer {

    private final AtomicInteger forYes = new AtomicInteger();
    private final AtomicInteger forNo = new AtomicInteger();
    private final AtomicInteger requredVotes = new AtomicInteger();

    private final AtomicBoolean succeed = new AtomicBoolean();
    private final List<User> voters = new ArrayList<>();

    private final VoteEmbedResponse response;
    private final CommandEventWrapper event;
    private final BotConfiguration config;
    private final RemotePropertyHandler handler;

    private final Function<Message, RestAction<List<Void>>> emojisFunctor = message -> RestAction.allOf(
        message.addReaction(UnicodeEmoji.THUMBS_UP.getCode()),
        message.addReaction(UnicodeEmoji.THUMBS_DOWN.getCode())
    );

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public VotingSystemSequencer(
        VoteEmbedResponse response, CommandEventWrapper event, BotConfiguration config, RemotePropertyHandler handler
    ) {
        this.response = response;
        this.event = event;
        this.config = config;
        this.handler = handler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initializeAndStartVoting() {
        final Function<Message, VotePredictorData> fabricatePredicateData = message ->
            new VotePredictorData(forYes, forNo, requredVotes, succeed, voters, response, message, event);
        JDALog.info(log, event, "Initialized voting from: '%s'", response.initializedClazz().getName());

        if (event.isFromSlashCommand() && !event.getSlashCommandEvent().getHook().isExpired()) {
            event.getSlashCommandEvent().getHook().sendMessageEmbeds(response.initiateEmbedMessage())
                .queue(message -> emojisFunctor.apply(message)
                    .queue(ignored -> fabricateEventWaiter(fabricatePredicateData.apply(message))));
            return;
        }
        event.getTextChannel().sendMessageEmbeds(response.initiateEmbedMessage())
            .queue(message -> emojisFunctor.apply(message)
                .queue(ignored -> fabricateEventWaiter(fabricatePredicateData.apply(message))));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void fabricateEventWaiter(VotePredictorData predictorData) {
        final long elapsedTimeInSec = handler
            .getPossibleRemoteProperty(RemoteProperty.R_INACTIVITY_VOTING_TIMEOUT, event.getGuild(), Long.class);
        config.getEventWaiter().waitForEvent(
            GuildMessageReactionAddEvent.class,
            e -> onAfterVotedPredicate(e, predictorData),
            e -> onAfterFinishVotingConsumer(predictorData),
            elapsedTimeInSec,
            TimeUnit.SECONDS,
            () -> onAfterTimeoutVotingRunnable(predictorData)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean onAfterVotedPredicate(GuildMessageReactionAddEvent e, VotePredictorData predictorData) {
        if (!e.getMessageId().equals(predictorData.message().getId())) return false;
        if (e.getUser().isBot()) return false;

        final MessageReaction.ReactionEmote emote = e.getReactionEmote();
        if (!emote.isEmoji()) return false;

        final VoiceChannel voiceChannelWithBot = e.getGuild().getVoiceChannels().stream()
            .filter(c -> c.getMembers().contains(e.getGuild().getSelfMember()))
            .findFirst()
            .orElseThrow(() -> new UserOnVoiceChannelWithBotNotFoundException(config, predictorData.wrapper()));

        if (!voiceChannelWithBot.getMembers().contains(e.getMember())) {
            removeEmoji(e, predictorData, UnicodeEmoji.THUMBS_UP);
            removeEmoji(e, predictorData, UnicodeEmoji.THUMBS_DOWN);
            return false;
        }

        if (predictorData.votedUsers().contains(e.getUser())) {
            if (UnicodeEmoji.THUMBS_DOWN.checkEquals(emote)) {
                removeEmoji(e, predictorData, UnicodeEmoji.THUMBS_UP);
                predictorData.votesForYes().decrementAndGet();
                predictorData.votesForNo().incrementAndGet();
                JDALog.info(log, event, "Member '%s' was re-voted for NO from YES", e.getUser().getAsTag());
            } else {
                removeEmoji(e, predictorData, UnicodeEmoji.THUMBS_DOWN);
                predictorData.votesForYes().incrementAndGet();
                predictorData.votesForNo().decrementAndGet();
                JDALog.info(log, event, "Member '%s' was re-voted for YES from NO", e.getUser().getAsTag());
            }
        } else {
            if (UnicodeEmoji.THUMBS_DOWN.checkEquals(emote)) {
                predictorData.votesForNo().incrementAndGet();
                JDALog.info(log, event, "Member '%s' was voted for NO", e.getUser().getAsTag());
            } else {
                predictorData.votesForYes().incrementAndGet();
                JDALog.info(log, event, "Member '%s' was voted for YES", e.getUser().getAsTag());
            }
            predictorData.votedUsers().add(e.getUser());
        }

        final Member botMember = e.getGuild().getSelfMember();
        final int totalMembersOnChannel = (int) voiceChannelWithBot.getMembers().stream()
            .filter(m -> !m.getUser().isBot() && !botMember.equals(m))
            .count();
        if (totalMembersOnChannel == 0) return false;

        final byte percentageRatio = handler // 0 - 100
            .getPossibleRemoteProperty(RemoteProperty.R_VOTING_PERCENTAGE_RATIO, event.getGuild(), Byte.class);
        final double differentYesRatio = (1.0 * predictorData.votesForYes().get() / totalMembersOnChannel) * 100;
        final double differentNoRatio = (1.0 * predictorData.votesForNo().get() / totalMembersOnChannel) * 100;

        predictorData.requiredVotes().set((int) Math.ceil(totalMembersOnChannel * (percentageRatio * 1.0 / 100)));

        if (differentYesRatio >= percentageRatio) {
            predictorData.succeed().set(true);
            return true;
        }
        if (differentNoRatio >= percentageRatio) {
            predictorData.succeed().set(false);
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void onAfterFinishVotingConsumer(VotePredictorData predictorData) {
        clearData(predictorData);

        final VoteFinishEmbedData embedData = new VoteFinishEmbedData(predictorData);
        if (predictorData.succeed().get()) {
            JDALog.info(log, predictorData.wrapper(), "Voting execution was ended with successfully result");
            sendEmbed(predictorData, predictorData.response().onSuccessVotingAction().apply(embedData));
        } else {
            JDALog.info(log, predictorData.wrapper(), "Voting execution was ended with failure result");
            sendEmbed(predictorData, predictorData.response().onFailureVotingAction().apply(embedData));
        }
        clearConcurentAtomicValues(predictorData);
    }

    private void onAfterTimeoutVotingRunnable(VotePredictorData predictorData) {
        clearData(predictorData);

        final VoteFinishEmbedData embedData = new VoteFinishEmbedData(predictorData);
        JDALog.info(log, predictorData.wrapper(), "Voting execution was timeouted");

        sendEmbed(predictorData, predictorData.response().onTimeoutVotingAction().apply(embedData));

        clearConcurentAtomicValues(predictorData);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void clearData(VotePredictorData predictorData) {
        predictorData.message().clearReactions().queue();
        predictorData.votedUsers().clear();
    }

    private void clearConcurentAtomicValues(VotePredictorData predictorData) {
        predictorData.votesForYes().set(0);
        predictorData.votesForNo().set(0);
        predictorData.succeed().set(false);
        predictorData.requiredVotes().set(0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendEmbed(VotePredictorData predictorData, MessageEmbed messageEmbed) {
        final CommandEventWrapper event = predictorData.wrapper();
        if (event.isFromSlashCommand() && !event.getSlashCommandEvent().getHook().isExpired()) {
            event.getSlashCommandEvent().getHook().sendMessageEmbeds(messageEmbed).queue();
            return;
        }
        event.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void removeEmoji(GuildMessageReactionAddEvent e, VotePredictorData predictorData, UnicodeEmoji emoji) {
        predictorData.message().removeReaction(emoji.getCode(), e.getUser()).queue();
    }
}
