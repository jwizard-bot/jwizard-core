/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioMoveBotToVoiceChannelCommandExecutor.java
 * Last modified: 15/07/2022, 02:29
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

package pl.miloszgilga.franekbotapp.executors.audioplayer;

import jdk.jfr.Description;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.entities.VoiceChannel;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;
import java.util.EnumSet;
import java.util.Objects;
import java.util.ArrayList;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.exceptions.MusicBotNotActiveException;
import pl.miloszgilga.franekbotapp.exceptions.UserOnVoiceChannelNotFoundException;
import pl.miloszgilga.franekbotapp.exceptions.UnableAccessToInvokeCommandException;

import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
import static pl.miloszgilga.franekbotapp.BotCommand.MUSIC_JOIN;
import static net.dv8tion.jda.api.Permission.VOICE_MOVE_OTHERS;


public class MoveBotToVoiceChannelCommandExecutor extends Command {

    private final LoggerFactory logger = new LoggerFactory(MoveBotToVoiceChannelCommandExecutor.class);

    private List<VoiceChannel> voiceChannels = new ArrayList<>();
    private Member senderUserMember;

    public MoveBotToVoiceChannelCommandExecutor() {
        name = MUSIC_JOIN.getCommandName();
        help = MUSIC_JOIN.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]join>")
    protected void execute(CommandEvent event) {
        try {
            senderUserMember = event.getGuild().getMember(event.getAuthor());
            final Member botMember = event.getGuild().getMember(event.getJDA().getSelfUser());
            voiceChannels = Objects.requireNonNull(event.getJDA().getGuildById(event.getGuild().getId()))
                    .getVoiceChannels();

            if (senderUserMember == null || botMember == null) {
                throw new UnableAccessToInvokeCommandException(event, "rangi z możliwością przenoszenia " +
                        "użytkowników na inny kanał głosowy lub rangę administratora.");
            }

            VoiceChannel botChannel = voiceChannels.stream()
                    .filter(channel -> channel.getMembers().contains(botMember)).findFirst()
                    .orElseThrow(() -> { throw new MusicBotNotActiveException(event); });

            final EnumSet<Permission> userPermissions = senderUserMember.getPermissions();
            if (!userPermissions.contains(VOICE_MOVE_OTHERS) && !userPermissions.contains(ADMINISTRATOR)) {
                throw new UnableAccessToInvokeCommandException(event, "rangi z możliwością przenoszenia " +
                        "użytkowników na inny kanał głosowy lub rangę administratora.");
            }

            VoiceChannel voiceChannel = findVoiceChannelWhereUserIs(event);
            if (botChannel.equals(voiceChannel)) return;

            event.getGuild().moveVoiceMember(botMember, voiceChannel).complete();

            final var embedMessage = new EmbedMessage("INFO", String.format(
                    "Bot został przeniesiony na kanał głosowy **%s**.", voiceChannel.getName()),
                    EmbedMessageColor.GREEN);
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

            logger.info(String.format("Przeniesienie bota muzycznego na kanał '%s' przez '%s'", voiceChannel.getName(),
                    event.getAuthor().getAsTag()), event.getGuild());

        } catch (UnableAccessToInvokeCommandException | UserOnVoiceChannelNotFoundException |
                 MusicBotNotActiveException ex) {
            logger.warn(ex.getMessage(), event.getGuild());
        }
    }

    private VoiceChannel findVoiceChannelWhereUserIs(CommandEvent event) {
        return voiceChannels.stream()
                .filter(channel -> channel.getMembers().contains(senderUserMember))
                .findFirst().orElseThrow(() -> {
                    throw new UserOnVoiceChannelNotFoundException(event, "Aby przenieść bota muzycznego musisz" +
                            "przebywać na którymś z kanałów głosowych");
                });
    }
}